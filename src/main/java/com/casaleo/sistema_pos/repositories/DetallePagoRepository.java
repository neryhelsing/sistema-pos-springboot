package com.casaleo.sistema_pos.repositories;

import com.casaleo.sistema_pos.models.DetallePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DetallePagoRepository extends JpaRepository<DetallePago, Integer> {
    List<DetallePago> findByPago_Id(Integer pagoId);
    List<DetallePago> findByFactura_Id(Integer facturaId);

    // ✅ SUMA de monto_aplicado por factura
    @Query("SELECT COALESCE(SUM(d.montoAplicado), 0) FROM DetallePago d WHERE d.factura.id = :facturaId")
    BigDecimal sumMontoAplicadoByFacturaId(@Param("facturaId") Integer facturaId);
}
