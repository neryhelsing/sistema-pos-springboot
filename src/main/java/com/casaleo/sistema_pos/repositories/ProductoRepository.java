package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * Buscar productos que contengan una cadena específica en su nombre, ignorando mayúsculas/minúsculas.
     *
     * @param nombre   Cadena de búsqueda.
     * @param pageable Parámetro de paginación.
     * @return Página de productos que coincidan.
     */
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Buscar productos que contengan una cadena específica en su código de barra.
     *
     * @param codigoBarra Subcadena del código de barra.
     * @param pageable    Parámetro de paginación.
     * @return Página de productos que coincidan.
     */
    Page<Producto> findByCodigoBarraContaining(String codigoBarra, Pageable pageable);

    /**
     * Obtener todos los productos con paginación.
     *
     * @param pageable Parámetro de paginación.
     * @return Página de productos.
     */
    Page<Producto> findAll(Pageable pageable);

    /**
     * Verificar si existe un producto con un código de barra específico.
     *
     * @param codigoBarra Código de barra a verificar.
     * @return true si el código de barra ya existe, false en caso contrario.
     */
    boolean existsByCodigoBarra(String codigoBarra);
}
