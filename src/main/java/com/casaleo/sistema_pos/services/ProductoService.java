package com.casaleo.sistema_pos.services;

import com.casaleo.sistema_pos.models.Producto;
import com.casaleo.sistema_pos.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Buscar productos que contengan una cadena específica en su nombre con paginación.
     *
     * @param query   Cadena de búsqueda.
     * @param page    Número de página.
     * @param size    Cantidad de elementos por página.
     * @return Página de productos que coincidan.
     */
    public Page<Producto> buscarPorNombre(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findByNombreContainingIgnoreCase(query, pageable);
    }

    /**
     * Buscar productos que contengan una subcadena en su código de barra con paginación.
     *
     * @param query   Subcadena del código de barra.
     * @param page    Número de página.
     * @param size    Cantidad de elementos por página.
     * @return Página de productos que coincidan.
     */
    public Page<Producto> buscarPorCodigoBarra(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findByCodigoBarraContaining(query, pageable);
    }

    /**
     * Obtener todos los productos con paginación.
     *
     * @param page    Número de página.
     * @param size    Cantidad de elementos por página.
     * @return Página de todos los productos.
     */
    public Page<Producto> obtenerTodosLosProductos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findAll(pageable);
    }

    /**
     * Guardar un nuevo producto en la base de datos después de validar sus datos.
     *
     * @param producto Producto a guardar.
     * @return Producto guardado.
     */
    public Producto guardarProducto(Producto producto) {
        // 🔹 Verificar si el código de barra no es nulo ni vacío antes de validar duplicados
        if (producto.getCodigoBarra() != null && !producto.getCodigoBarra().trim().isEmpty()) {
            if (productoRepository.existsByCodigoBarra(producto.getCodigoBarra())) {
                throw new IllegalArgumentException("El código de barra ya está registrado.");
            }
        }

        // Validaciones adicionales
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        // Guardar producto en la base de datos
        return productoRepository.save(producto);
    }

    public Producto obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto actualizarProducto(Integer id, Producto producto) {
        // Verificar si el producto existe
        if (!productoRepository.existsById(id)) {
            return null; // Producto no encontrado
        }

        // Validaciones
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        // Guardar los cambios
        producto.setId(id); // Asegurar que el ID se mantiene
        return productoRepository.save(producto);
    }

    public boolean eliminarProducto(Integer id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
