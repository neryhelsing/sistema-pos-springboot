package com.casaleo.sistema_pos.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // DB: cliente_id (NOT NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // DB: n_pago (NOT NULL, UNIQUE)
    @Column(name = "n_pago", nullable = true, unique = true)
    private String nPago;

    // DB: metodo (NOT NULL) enum('EFECTIVO','TRANSFERENCIA')
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false)
    private MetodoPago metodo;

    // DB: banco (NULL)
    @Column(name = "banco", nullable = true)
    private String banco;

    // DB: n_operacion (NULL)
    @Column(name = "n_operacion", nullable = true)
    private String nOperacion;

    // DB: total_pagado (NOT NULL)
    @Column(name = "total_pagado", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPagado;

    @Column(name = "monto_entregado", precision = 15, scale = 2, nullable = true)
    private BigDecimal montoEntregado;

    @Column(name = "vuelto", precision = 15, scale = 2, nullable = true)
    private BigDecimal vuelto = BigDecimal.ZERO;

    @Column(name = "monto_transferido", precision = 15, scale = 2, nullable = true)
    private BigDecimal montoTransferido;

    @Column(name = "efectivo_devuelto", precision = 15, scale = 2, nullable = true)
    private BigDecimal efectivoDevuelto = BigDecimal.ZERO;

    // DB: creado_en timestamp DEFAULT current_timestamp()
    // Lo maneja la DB -> JPA solo lee
    @Column(name = "creado_en", nullable = false, updatable = false, insertable = false)
    private LocalDateTime creadoEn;

    // DB: actualizado_en timestamp DEFAULT current_timestamp() ON UPDATE CURRENT_TIMESTAMP()
    // Lo maneja la DB -> JPA solo lee
    @Column(name = "actualizado_en", nullable = false, updatable = false, insertable = false)
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePago> detalles = new ArrayList<>();

    public Pago() {}

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNPago() {
        return nPago;
    }

    public void setNPago(String nPago) {
        this.nPago = nPago;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNOperacion() {
        return nOperacion;
    }

    public void setNOperacion(String nOperacion) {
        this.nOperacion = nOperacion;
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }

    public BigDecimal getMontoEntregado() {
        return montoEntregado;
    }

    public void setMontoEntregado(BigDecimal montoEntregado) {
        this.montoEntregado = montoEntregado;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }

    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }

    public BigDecimal getMontoTransferido() {
        return montoTransferido;
    }

    public void setMontoTransferido(BigDecimal montoTransferido) {
        this.montoTransferido = montoTransferido;
    }

    public BigDecimal getEfectivoDevuelto() {
        return efectivoDevuelto;
    }

    public void setEfectivoDevuelto(BigDecimal efectivoDevuelto) {
        this.efectivoDevuelto = efectivoDevuelto;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public List<DetallePago> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePago> detalles) {
        this.detalles = detalles;
    }
}
