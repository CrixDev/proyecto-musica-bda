package com.equipo3.bibliotecamusical.dtos;

/**
 * Datos de una cancion. {@code id} es el hex del {@code _id} embebido (null al
 * crear una cancion nueva; el negocio lo genera).
 */
public record CancionDTO(
        String id,
        String nombre,
        int numeroPista,
        int duracionSegundos,
        String genero) {
}
