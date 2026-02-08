package com.casaleo.sistema_pos.dto;

import java.math.BigDecimal;

public class PagoDetalleResponseDTO {
    private Integer facturaId;
    private String numeroFactura;
    private String fechaEmision;
    private BigDecimal totalFactura;
    private BigDecimal saldoActual;
    private BigDecimal montoAplicado;

    public PagoDetalleResponseDTO(Integer facturaId, String numeroFactura, String fechaEmision,
                                  BigDecimal totalFactura, BigDecimal saldoActual, BigDecimal montoAplicado) {
        this.facturaId = facturaId;
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.totalFactura = totalFactura;
        this.saldoActual = saldoActual;
        this.montoAplicado = montoAplicado;
    }

    public Integer getFacturaId() { return facturaId; }
    public String getNumeroFactura() { return numeroFactura; }
    public String getFechaEmision() { return fechaEmision; }
    public BigDecimal getTotalFactura() { return totalFactura; }
    public BigDecimal getSaldoActual() { return saldoActual; }
    public BigDecimal getMontoAplicado() { return montoAplicado; }
}
