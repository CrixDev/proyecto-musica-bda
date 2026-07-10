package com.equipo3.bibliotecamusical.negocio.servicios;

/**
 * Filtro de tipo de contenido para el buscador: permite acotar los resultados a
 * "solo artistas", "solo albumes" o "solo canciones", ademas del modo TODOS.
 */
public enum TipoContenido {

    TODOS("Todo"),
    ARTISTA("Solo artistas"),
    ALBUM("Solo álbumes"),
    CANCION("Solo canciones");

    private final String etiqueta;

    TipoContenido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
