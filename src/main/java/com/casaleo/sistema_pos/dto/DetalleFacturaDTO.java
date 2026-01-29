package com.casaleo.sistema_pos.dto;

import java.math.BigDecimal;


public class DetalleFacturaDTO {
    private Integer productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    // GETTERS Y SETTERS

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

}
