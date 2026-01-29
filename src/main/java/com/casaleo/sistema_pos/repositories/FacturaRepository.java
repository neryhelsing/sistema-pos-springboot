package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.math.BigDecimal;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    // Pendientes
    List<Factura> findByCliente_IdAndSaldoGreaterThan(Integer clienteId, BigDecimal saldo);

    // ✅ Búsqueda por N° factura o por datos del cliente
    Page<Factura> findByNumeroFacturaContainingIgnoreCaseOrCliente_NombreContainingIgnoreCaseOrCliente_RucContainingIgnoreCase(
            String numeroFactura,
            String clienteNombre,
            String clienteRuc,
            Pageable pageable
    );
}
