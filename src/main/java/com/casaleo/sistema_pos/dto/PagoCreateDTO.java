package com.casaleo.sistema_pos.dto;

import java.util.List;
import java.math.BigDecimal;

import com.casaleo.sistema_pos.models.MetodoPago;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PagoCreateDTO {

    private Integer clienteId;
    private MetodoPago metodo;

    // ===== TRANSFERENCIA =====
    private String banco;

    @JsonProperty("nOperacion")
    @JsonAlias({"NOperacion", "operacion", "n_operacion"})
    private String nOperacion;

    private BigDecimal montoTransferido;

    // ===== EFECTIVO =====
    private BigDecimal montoEntregado;
    private BigDecimal efectivoDevuelto;

    // ===== FACTURAS =====
    private List<PagoDetalleCreateDTO> detalles;

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getNOperacion() { return nOperacion; }
    public void setNOperacion(String nOperacion) { this.nOperacion = nOperacion; }

    public BigDecimal getMontoTransferido() { return montoTransferido; }
    public void setMontoTransferido(BigDecimal montoTransferido) { this.montoTransferido = montoTransferido; }

    public BigDecimal getMontoEntregado() { return montoEntregado; }
    public void setMontoEntregado(BigDecimal montoEntregado) { this.montoEntregado = montoEntregado; }

    public BigDecimal getEfectivoDevuelto() { return efectivoDevuelto; }
    public void setEfectivoDevuelto(BigDecimal efectivoDevuelto) { this.efectivoDevuelto = efectivoDevuelto; }

    public List<PagoDetalleCreateDTO> getDetalles() { return detalles; }
    public void setDetalles(List<PagoDetalleCreateDTO> detalles) { this.detalles = detalles; }
}
