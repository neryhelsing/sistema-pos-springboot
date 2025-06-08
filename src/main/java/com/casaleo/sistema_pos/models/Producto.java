package com.casaleo.sistema_pos.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos") // Asocia la clase con la tabla "productos"
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera IDs automáticamente
    private int id;

    @Column(name = "codigo_barra", unique = true, length = 100) // Código de barra opcional (permite valores nulos)
    private String codigoBarra;

    @Column(nullable = false, length = 255) // Columna "nombre", no permite valores nulos
    private String nombre;

    @Column(nullable = false) // Columna "cantidad", no permite valores nulos
    private int cantidad;

    @Column(nullable = false) // Columna "precio", no permite valores nulos
    private int precio; // Cambiar a "double" si manejas decimales

    @Column(nullable = false)
    private int gravado; // Puede ser 5 o 10

    @Column(length = 255) // Columna "descripcion", permite valores nulos
    private String descripcion;

    @Column(name = "creado_en", updatable = false) // Columna "creado_en", no se actualiza
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en") // Columna "actualizado_en"
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "producto")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<DetalleFactura> detalles;

    // ✅ CONSTRUCTOR POR ID PARA USO EN setProducto(new Producto(id))
    public Producto(Integer id) {
        this.id = id;
    }

    // ✅ Constructor vacío (necesario para JPA)
    public Producto() {}

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getGravado() {
        return gravado;
    }

    public void setGravado(int gravado) {
        if (gravado != 5 && gravado != 10) {
            throw new IllegalArgumentException("El gravado debe ser 5 o 10.");
        }
        this.gravado = gravado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
