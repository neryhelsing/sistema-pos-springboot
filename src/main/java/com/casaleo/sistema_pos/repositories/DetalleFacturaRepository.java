package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM DetalleFactura d WHERE d.factura.id = :facturaId")
    void deleteByFacturaId(Integer facturaId);
}

