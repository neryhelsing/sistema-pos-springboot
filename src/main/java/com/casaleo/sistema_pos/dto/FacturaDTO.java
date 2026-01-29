package com.casaleo.sistema_pos.dto;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;


public class FacturaDTO {
    private String numeroFactura;
    private Date fechaEmision;
    private Integer clienteId;
    private BigDecimal total;
    private String estado;
    private String estadoPago;
    private String tipo; // FCC o FCR
    private BigDecimal saldo;
    private BigDecimal montoAplicado; // ✅ NUEVO (Pagado)
    private List<DetalleFacturaDTO> detalles;


    // GETTERS Y SETTERS

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoPago() { return estadoPago; }

    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    // Getter/Setter
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<DetalleFacturaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaDTO> detalles) {
        this.detalles = detalles;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getMontoAplicado() {
        return montoAplicado;
    }

    public void setMontoAplicado(BigDecimal montoAplicado) {
        this.montoAplicado = montoAplicado;
    }
}
