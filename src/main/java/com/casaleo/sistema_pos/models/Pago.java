package com.casaleo.sistema_pos.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "n_pago", nullable = false, unique = true)
    private String nPago;

    // DB: metodo (NOT NULL) enum('EFECTIVO','TRANSFERENCIA')
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false)
    private MetodoPago metodo;

    // DB: banco (NULL)
    @Column(name = "banco")
    private String banco;

    // DB: n_operacion (NULL)
    @Column(name = "n_operacion")
    private String nOperacion;

    // DB: total_pagado (NOT NULL)
    @Column(name = "total_pagado", nullable = false)
    private Double totalPagado;

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

    public Double getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(Double totalPagado) {
        this.totalPagado = totalPagado;
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
