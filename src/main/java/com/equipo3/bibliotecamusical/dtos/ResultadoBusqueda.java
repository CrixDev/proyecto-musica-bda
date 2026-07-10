package com.equipo3.bibliotecamusical.dtos;

import java.util.List;

/**
 * Resultado agregado de una busqueda: las tres colecciones que puede devolver
 * el buscador global (artistas, albumes y canciones), ya filtradas y ordenadas.
 */
public record ResultadoBusqueda(
        List<ArtistaDTO> artistas,
        List<AlbumDTO> albumes,
        List<CancionResultadoDTO> canciones) {

    public boolean vacio() {
        return artistas.isEmpty() && albumes.isEmpty() && canciones.isEmpty();
    }

    public int total() {
        return artistas.size() + albumes.size() + canciones.size();
    }
}
