package com.casaleo.sistema_pos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.casaleo.sistema_pos.models.MetodoPago;

public class PagoResponseDTO {
    private Integer id;
    private String nPago;
    private Double totalPagado;
    private String creadoEn;
    private MetodoPago metodo;

    public PagoResponseDTO(Integer id, String nPago, Double totalPagado, LocalDateTime creadoEn, MetodoPago metodo) {
        this.id = id;
        this.nPago = nPago;
        this.totalPagado = totalPagado;
        this.metodo = metodo;

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

    public Double getTotalPagado() {
        return totalPagado;
    }

    public String getCreadoEn() {
        return creadoEn;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }
}
