package com.poo.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SeguridadRolesFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String metodo = request.getMethod();

        // Solo aplicamos la validación de roles a los catálogos REST (/api/...)
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtenemos el rol sin exigir usuario/contraseña (por Header 'X-Rol' o parámetro '?rol=')
        String rol = request.getHeader("X-Rol");
        if (rol == null || rol.isBlank()) {
            rol = request.getParameter("rol");
        }

        if (rol == null || rol.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Debe indicar un rol (USUARIO o ADMINISTRADOR) usando el parámetro ?rol= o el Header X-Rol\"}");
            return;
        }

        rol = rol.toUpperCase().trim();

        // REGLA 1: El ADMINISTRADOR tiene acceso completo (CRUD) a Usuarios y Productos
        if ("ADMINISTRADOR".equals(rol)) {
            filterChain.doFilter(request, response);
            return;
        }

        // REGLA 2: El USUARIO solo puede acceder a Usuarios únicamente para AGREGAR (POST)
        if ("USUARIO".equals(rol)) {
            if (path.startsWith("/api/usuarios") && "POST".equalsIgnoreCase(metodo)) {
                filterChain.doFilter(request, response);
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Acceso denegado (403): El rol USUARIO solo tiene permiso para agregar nuevos usuarios (POST /api/usuarios).\"}");
                return;
            }
        }

        // Si envía un rol no reconocido
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"Rol no válido. Use USUARIO o ADMINISTRADOR.\"}");
    }
}