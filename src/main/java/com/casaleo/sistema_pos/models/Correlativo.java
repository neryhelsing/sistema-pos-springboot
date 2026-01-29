package com.casaleo.sistema_pos.models;

import jakarta.persistence.*;

@Entity
@Table(name = "correlativos")
public class Correlativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String clave;

    @Column(name = "siguiente_numero", nullable = false)
    private Long siguienteNumero;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Long getSiguienteNumero() { return siguienteNumero; }
    public void setSiguienteNumero(Long siguienteNumero) { this.siguienteNumero = siguienteNumero; }
}
