package com.equipo3.bibliotecamusical.dtos;

import java.time.LocalDate;

/** Datos de un integrante de banda para intercambio con la presentacion. */
public record IntegranteDTO(
        String nombreCompleto,
        String rol,
        LocalDate fechaIngreso,
        LocalDate fechaSalida,
        boolean activo) {
}
