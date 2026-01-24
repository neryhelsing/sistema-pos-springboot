package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.DetallePago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetallePagoRepository extends JpaRepository<DetallePago, Integer> {
    List<DetallePago> findByPago_Id(Integer pagoId);
    List<DetallePago> findByFactura_Id(Integer facturaId);
}
