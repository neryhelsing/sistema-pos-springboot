package com.casaleo.sistema_pos.dto;

public class PagoDetalleCreateDTO {

    private Integer facturaId;
    private Double montoAplicado;

    public Integer getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Integer facturaId) {
        this.facturaId = facturaId;
    }

    public Double getMontoAplicado() {
        return montoAplicado;
    }

    public void setMontoAplicado(Double montoAplicado) {
        this.montoAplicado = montoAplicado;
    }
}
