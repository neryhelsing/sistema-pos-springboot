package com.casaleo.sistema_pos.dto;

import com.casaleo.sistema_pos.models.EstadoPago;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.casaleo.sistema_pos.models.MetodoPago;

import java.math.BigDecimal;

public class PagoResponseDTO {
    private Integer id;
    private String nPago;
    private BigDecimal totalPagado;
    private String creadoEn;
    private MetodoPago metodo;
    private EstadoPago estado;

    public PagoResponseDTO(Integer id, String nPago, BigDecimal totalPagado, LocalDateTime creadoEn, MetodoPago metodo, EstadoPago estado) {
        this.id = id;
        this.nPago = nPago;
        this.totalPagado = totalPagado;
        this.metodo = metodo;
        this.estado = estado;

        // ✅ Evitar NullPointerException si DB aún no devolvió el timestamp
        if (creadoEn != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            this.creadoEn = creadoEn.format(formatter);
        } else {
            this.creadoEn = null;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getNPago() {
        return nPago;
    }

    public BigDecimal getTotalPagado() {
        return totalPagado;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public EstadoPago getEstado() { return estado; }
}
