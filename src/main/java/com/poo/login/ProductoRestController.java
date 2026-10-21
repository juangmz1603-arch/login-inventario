package com.poo.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;

    // CONSULTAR TODOS (Solo ADMINISTRADOR)
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // CONSULTAR POR ID (Solo ADMINISTRADOR)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // CREAR PRODUCTO (Solo ADMINISTRADOR)
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto nuevoProducto) {
        Producto guardado = productoRepository.save(nuevoProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // ACTUALIZAR PRODUCTO (Solo ADMINISTRADOR)
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto datos) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(datos.getNombre());
            producto.setPrecio(datos.getPrecio());
            producto.setStock(datos.getStock());
            return ResponseEntity.ok(productoRepository.save(producto));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ELIMINAR PRODUCTO (Solo ADMINISTRADOR)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}