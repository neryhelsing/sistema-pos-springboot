package com.casaleo.sistema_pos.dto;

import java.util.List;

import com.casaleo.sistema_pos.models.MetodoPago;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PagoCreateDTO {

    private Integer clienteId;
    private MetodoPago metodo;
    private String banco;

    // ✅ Fuerza a leer desde "nOperacion" (y acepta variantes)
    @JsonProperty("nOperacion")
    @JsonAlias({"NOperacion", "operacion", "n_operacion"})
    private String nOperacion;

    private List<PagoDetalleCreateDTO> detalles;

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getNOperacion() { return nOperacion; }
    public void setNOperacion(String nOperacion) { this.nOperacion = nOperacion; }

    public List<PagoDetalleCreateDTO> getDetalles() { return detalles; }
    public void setDetalles(List<PagoDetalleCreateDTO> detalles) { this.detalles = detalles; }
}
