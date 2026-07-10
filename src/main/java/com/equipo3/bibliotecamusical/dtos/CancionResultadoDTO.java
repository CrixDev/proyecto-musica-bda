package com.equipo3.bibliotecamusical.dtos;

import java.time.LocalDate;

/**
 * Cancion resuelta junto con el contexto de su album y artista, para poder
 * mostrarla en resultados de busqueda y navegar a su album al hacer clic.
 * Las canciones viven embebidas dentro del album, por eso se aplana aqui la
 * informacion necesaria para pintarlas de forma independiente.
 */
public record CancionResultadoDTO(
        String cancionId,
        String nombre,
        int numeroPista,
        int duracionSegundos,
        String genero,
        String albumId,
        String albumNombre,
        String imagenPortada,
        String artistaId,
        String artistaNombre,
        LocalDate fechaLanzamiento) {
}
