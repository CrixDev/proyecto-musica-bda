package com.equipo3.bibliotecamusical.dtos;

import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import java.time.LocalDate;
import java.util.List;

/**
 * Datos de un artista. {@code id} es el hex del {@code _id} (null al crear).
 * {@code integrantes} solo aplica cuando {@code tipo == BANDA}.
 */
public record ArtistaDTO(
        String id,
        TipoArtista tipo,
        String nombre,
        String imagen,
        String genero,
        LocalDate fechaCreacion,
        List<IntegranteDTO> integrantes) {
}
