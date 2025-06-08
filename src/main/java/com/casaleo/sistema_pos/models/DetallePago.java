package com.casaleo.sistema_pos.models;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pago")
public class DetallePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodo; // EFECTIVO o TRANSFERENCIA

    @Column(name = "monto_efectivo")
    private Double montoEfectivo;

    @Column(name = "monto_transferencia")
    private Double montoTransferencia;

    @Column
    private String banco;

    @Column(name = "n_operacion")
    private String nOperacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    public DetallePago() {}

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public Double getMontoEfectivo() {
        return montoEfectivo;
    }

    public void setMontoEfectivo(Double montoEfectivo) {
        this.montoEfectivo = montoEfectivo;
    }

    public Double getMontoTransferencia() {
        return montoTransferencia;
    }

    public void setMontoTransferencia(Double montoTransferencia) {
        this.montoTransferencia = montoTransferencia;
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

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
}
