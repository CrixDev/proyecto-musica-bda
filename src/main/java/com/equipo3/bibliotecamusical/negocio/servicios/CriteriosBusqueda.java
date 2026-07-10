package com.equipo3.bibliotecamusical.negocio.servicios;

/**
 * Parametros de una busqueda: texto libre + filtros opcionales (tipo de
 * contenido, orden, anio y genero). Inmutable; usar los metodos {@code conX}
 * para derivar variantes sin mutar la instancia original.
 *
 * @param texto  texto a buscar (nombre de artista/banda/integrante, album o cancion). Puede ir vacio.
 * @param tipo   acota a solo artistas/albumes/canciones o TODOS.
 * @param orden  criterio de ordenamiento de los resultados.
 * @param anio   filtra por anio (de lanzamiento del album/cancion o de creacion del artista); {@code null} = sin filtro.
 * @param genero filtra por genero exacto; {@code null} o vacio = sin filtro.
 */
public record CriteriosBusqueda(
        String texto,
        TipoContenido tipo,
        OrdenBusqueda orden,
        Integer anio,
        String genero) {

    /** Criterios por defecto: sin texto, TODOS, ordenado por relevancia y sin filtros. */
    public static CriteriosBusqueda vacio() {
        return new CriteriosBusqueda("", TipoContenido.TODOS, OrdenBusqueda.RELEVANCIA, null, null);
    }

    public CriteriosBusqueda conTexto(String nuevoTexto) {
        return new CriteriosBusqueda(nuevoTexto, tipo, orden, anio, genero);
    }

    public CriteriosBusqueda conTipo(TipoContenido nuevoTipo) {
        return new CriteriosBusqueda(texto, nuevoTipo, orden, anio, genero);
    }

    public CriteriosBusqueda conOrden(OrdenBusqueda nuevoOrden) {
        return new CriteriosBusqueda(texto, tipo, nuevoOrden, anio, genero);
    }

    public CriteriosBusqueda conAnio(Integer nuevoAnio) {
        return new CriteriosBusqueda(texto, tipo, orden, nuevoAnio, genero);
    }

    public CriteriosBusqueda conGenero(String nuevoGenero) {
        return new CriteriosBusqueda(texto, tipo, orden, anio, nuevoGenero);
    }
}
