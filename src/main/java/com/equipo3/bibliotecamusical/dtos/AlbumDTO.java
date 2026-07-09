package com.equipo3.bibliotecamusical.dtos;

import java.time.LocalDate;
import java.util.List;

/**
 * Datos de un album. {@code id} es el hex del {@code _id} (null al crear).
 * {@code artistaId} es el hex del artista al que pertenece.
 */
public record AlbumDTO(
        String id,
        String artistaId,
        String nombre,
        LocalDate fechaLanzamiento,
        String genero,
        String imagenPortada,
        List<CancionDTO> canciones) {
}
