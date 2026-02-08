package com.casaleo.sistema_pos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.casaleo.sistema_pos.models.EstadoPago;
import com.casaleo.sistema_pos.models.MetodoPago;

public class PagoViewDTO {
    private Integer id;
    private String nPago;
    private EstadoPago estado;
    private String creadoEn;
    private MetodoPago metodo;

    private Integer clienteId;
    private String clienteNombre;
    private String clienteRuc;

    private String banco;
    private String nOperacion;

    private BigDecimal totalPagado;
    private BigDecimal montoEntregado;
    private BigDecimal vuelto;
    private BigDecimal montoTransferido;
    private BigDecimal efectivoDevuelto;

    private List<PagoDetalleResponseDTO> detalles;

    public PagoViewDTO(Integer id, String nPago, EstadoPago estado, LocalDateTime creadoEn, MetodoPago metodo,
                       Integer clienteId, String clienteNombre, String clienteRuc,
                       String banco, String nOperacion,
                       BigDecimal totalPagado, BigDecimal montoEntregado, BigDecimal vuelto,
                       BigDecimal montoTransferido, BigDecimal efectivoDevuelto,
                       List<PagoDetalleResponseDTO> detalles) {
        this.id = id;
        this.nPago = nPago;
        this.estado = estado;
        this.metodo = metodo;

        if (creadoEn != null) {
            this.creadoEn = creadoEn.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            this.creadoEn = null;
        }

        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteRuc = clienteRuc;

        this.banco = banco;
        this.nOperacion = nOperacion;

        this.totalPagado = totalPagado;
        this.montoEntregado = montoEntregado;
        this.vuelto = vuelto;
        this.montoTransferido = montoTransferido;
        this.efectivoDevuelto = efectivoDevuelto;

        this.detalles = detalles;
    }

    public Integer getId() { return id; }
    public String getNPago() { return nPago; }
    public EstadoPago getEstado() { return estado; }
    public String getCreadoEn() { return creadoEn; }
    public MetodoPago getMetodo() { return metodo; }

    public Integer getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteRuc() { return clienteRuc; }

    public String getBanco() { return banco; }
    public String getNOperacion() { return nOperacion; }

    public BigDecimal getTotalPagado() { return totalPagado; }
    public BigDecimal getMontoEntregado() { return montoEntregado; }
    public BigDecimal getVuelto() { return vuelto; }
    public BigDecimal getMontoTransferido() { return montoTransferido; }
    public BigDecimal getEfectivoDevuelto() { return efectivoDevuelto; }

    public List<PagoDetalleResponseDTO> getDetalles() { return detalles; }
}
