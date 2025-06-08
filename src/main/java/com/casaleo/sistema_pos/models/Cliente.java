package com.casaleo.sistema_pos.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes") // Asocia esta clase con la tabla "clientes"

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera IDs automáticamente
    private int id;

    @Column(nullable = false, length = 255) // La columna "nombre" no permite valores nulos
    private String nombre;

    @Column(length = 20) // RUC con un límite de 20 caracteres
    private String ruc;

    @Column(length = 15) // Teléfono con un límite de 15 caracteres
    private String telefono;

    @Column(columnDefinition = "TEXT") // Dirección como texto largo
    private String direccion;

    @Column(length = 100) // Nueva columna para la ciudad del cliente
    private String ciudad;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    // ✅ CONSTRUCTOR POR ID PARA USO EN setCliente(new Cliente(id))
    public Cliente(Integer id) {
        this.id = id;
    }

    // ✅ Constructor vacío (necesario para JPA)
    public Cliente() {}

    // Getters y Setters (pueden ser generados automáticamente por tu IDE)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
