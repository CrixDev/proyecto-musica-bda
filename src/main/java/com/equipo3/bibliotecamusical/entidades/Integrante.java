package com.equipo3.bibliotecamusical.entidades;

import java.time.LocalDate;

/**
 * Integrante embebido dentro de un {@link Artista} de tipo BANDA.
 * Invariante de negocio: {@code activo == (fechaSalida == null)}.
 */
public class Integrante {

    private String nombreCompleto;
    private String rol;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private boolean activo;

    public Integrante() {
    }

    public Integrante(String nombreCompleto, String rol, LocalDate fechaIngreso,
                      LocalDate fechaSalida, boolean activo) {
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
