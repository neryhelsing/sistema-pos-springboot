package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Buscar clientes por nombre o RUC, ignorando mayúsculas/minúsculas, con paginación.
     *
     * @param nombre Nombre del cliente a buscar.
     * @param ruc    RUC del cliente a buscar.
     * @param pageable Parámetro de paginación.
     * @return Página de clientes que coincidan con los criterios.
     */
    Page<Cliente> findByNombreContainingIgnoreCaseOrRucContaining(String nombre, String ruc, Pageable pageable);

    /**
     * Obtener todos los clientes con paginación.
     *
     * @param pageable Parámetro de paginación.
     * @return Página de clientes.
     */
    Page<Cliente> findAll(Pageable pageable);

    /**
     * Verificar si existe un cliente con un RUC específico.
     *
     * @param ruc RUC del cliente a verificar.
     * @return true si el RUC ya está registrado, false en caso contrario.
     */
    boolean existsByRuc(String ruc);
}
