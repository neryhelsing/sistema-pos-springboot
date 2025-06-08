package com.casaleo.sistema_pos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagoResponseDTO {
    private Integer id;
    private String nPago;
    private Double totalPagado;
    private String creadoEn; // ← Corregido a String

    public PagoResponseDTO(Integer id, String nPago, Double totalPagado, LocalDateTime creadoEn) {
        this.id = id;
        this.nPago = nPago;
        this.totalPagado = totalPagado;

        // Formatear LocalDateTime como String ISO
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.creadoEn = creadoEn.format(formatter);
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
}
