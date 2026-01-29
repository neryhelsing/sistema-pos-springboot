package com.casaleo.sistema_pos.dto;

import com.casaleo.sistema_pos.models.Factura;
import java.util.Date;
import java.math.BigDecimal;

public class FacturaResponseDTO {
    private Integer id;
    private String numeroFactura;
    private Date fechaEmision;
    private BigDecimal total;
    private String estado;
    private String clienteNombre;
    private BigDecimal saldo;
    private Date creadoEn;

    // Constructor personalizado desde entidad Factura
    public FacturaResponseDTO(Factura f) {
        this.id = f.getId();
        this.numeroFactura = f.getNumeroFactura();
        this.fechaEmision = f.getFechaEmision();
        this.total = f.getTotal();
        this.saldo = f.getSaldo();
        this.estado = f.getEstado();
        this.clienteNombre = f.getCliente().getNombre();
        this.creadoEn = f.getCreadoEn();
    }

    // Constructor clásico
    public FacturaResponseDTO(Integer id, String numeroFactura, Date fechaEmision, BigDecimal total, BigDecimal saldo, String estado, String clienteNombre, Date creadoEn) {
        this.id = id;
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.total = total;
        this.saldo = saldo;
        this.estado = estado;
        this.clienteNombre = clienteNombre;
        this.creadoEn = creadoEn;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getEstado() {
        return estado;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public Date getCreadoEn() {
        return creadoEn;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
