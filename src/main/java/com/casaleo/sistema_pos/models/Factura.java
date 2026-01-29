package com.casaleo.sistema_pos.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_factura", nullable = true)
    private String numeroFactura;

    @Column(name = "fecha_emision", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creado_en",  insertable=false, updatable = false)
    private Date creadoEn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "actualizado_en", insertable=false, updatable=false)
    private Date actualizadoEn;

    @Column(nullable = false)
    private String estado;  // BORRADOR, EMITIDO, ANULADO, etc.

    @Column(name = "estado_pago", nullable = false)
    private String estadoPago; // PENDIENTE | PARCIAL | PAGADA

    @Column(nullable = false)
    private String tipo;  // FCC o FCR

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleFactura> detalles = new ArrayList<>();

    // GETTERS Y SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Date creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Date getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Date actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

}
