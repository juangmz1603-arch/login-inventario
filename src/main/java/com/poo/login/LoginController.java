package com.poo.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                Model model) {
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario != null && usuario.getPassword().equals(password)) {
            model.addAttribute("nombreUsuario", usuario.getUsername());
            model.addAttribute("rolUsuario", usuario.getRol());
            return "welcome";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    @PostMapping("/registrar")
    public String registrarNuevoUsuario(@RequestParam String nuevoUsername,
                                        @RequestParam String nuevoPassword,
                                        @RequestParam(defaultValue = "USUARIO") String nuevoRol,
                                        @RequestParam String usuarioActual,
                                        Model model) {

        if (usuarioRepository.findByUsername(nuevoUsername) != null) {
            model.addAttribute("errorRegistro", "El usuario '" + nuevoUsername + "' ya existe.");
        } else {
            Usuario nuevo = new Usuario();
            nuevo.setUsername(nuevoUsername);
            nuevo.setPassword(nuevoPassword);
            nuevo.setRol((nuevoRol != null && !nuevoRol.isBlank()) ? nuevoRol : "USUARIO");

            usuarioRepository.save(nuevo);

            model.addAttribute("mensajeExito", "¡Usuario '" + nuevoUsername + "' registrado con éxito con rol " + nuevo.getRol() + "!");
        }

        model.addAttribute("nombreUsuario", usuarioActual);
        return "welcome";
    }
}