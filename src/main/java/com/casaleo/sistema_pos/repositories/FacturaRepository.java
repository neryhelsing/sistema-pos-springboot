package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    List<Factura> findByCliente_IdAndSaldoGreaterThan(Long clienteId, Double saldo);
}
