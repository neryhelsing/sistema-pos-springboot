package com.casaleo.sistema_pos.dto;

import java.math.BigDecimal;

public class PagoDetalleCreateDTO {

    private Integer facturaId;
    private BigDecimal montoAplicado;

    public Integer getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Integer facturaId) {
        this.facturaId = facturaId;
    }

    public BigDecimal getMontoAplicado() {
        return montoAplicado;
    }

    public void setMontoAplicado(BigDecimal montoAplicado) {
        this.montoAplicado = montoAplicado;
    }
}
