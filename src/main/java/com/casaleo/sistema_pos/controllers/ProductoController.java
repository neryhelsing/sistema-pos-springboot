package com.casaleo.sistema_pos.controllers;

import com.casaleo.sistema_pos.models.Producto;
import com.casaleo.sistema_pos.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Buscar productos por nombre o código de barra con paginación.
     *
     * @param query  Cadena de búsqueda (nombre o código de barra).
     * @param page   Número de página (por defecto 0).
     * @param size   Cantidad de elementos por página (por defecto 10).
     * @return Página de productos que coincidan con los criterios de búsqueda.
     */
    @GetMapping
    public ResponseEntity<Page<Producto>> buscarProductos(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Producto> productos;

        if (query != null && !query.isEmpty()) {
            if (query.matches("\\d+")) { // Verifica si query es numérico (posible código de barra)
                productos = productoService.buscarPorCodigoBarra(query, page, size);
            } else {
                productos = productoService.buscarPorNombre(query, page, size);
            }
        } else {
            productos = productoService.obtenerTodosLosProductos(page, size);
        }

        return ResponseEntity.ok(productos);
    }

    /**
     * Agregar un nuevo producto a la base de datos.
     *
     * @param producto Datos del producto a agregar.
     * @return Respuesta con el producto guardado o un mensaje de error.
     */
    @PostMapping
    public ResponseEntity<?> agregarProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.guardarProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el producto.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Integer id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        return producto != null ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        return productoActualizado != null ? ResponseEntity.ok(productoActualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        if (productoService.eliminarProducto(id)) {
            return ResponseEntity.noContent().build(); // Código 204 si se eliminó con éxito
        } else {
            return ResponseEntity.notFound().build(); // Código 404 si no se encontró
        }
    }
}
