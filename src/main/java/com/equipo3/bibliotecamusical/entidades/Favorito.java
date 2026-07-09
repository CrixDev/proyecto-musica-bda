package com.equipo3.bibliotecamusical.entidades;

import java.time.LocalDate;
import org.bson.types.ObjectId;

/**
 * Favorito embebido dentro de un {@link Usuario}. Guarda una <em>referencia</em>
 * (no una copia) a la entidad favorita. {@code genero} y {@code fechaAgregado}
 * se denormalizan aqui para poder filtrar sin resolver referencias.
 * Para {@code tipo == CANCION}, {@code refId} es el id de la cancion y
 * {@code albumId} el id del album que la contiene.
 */
public class Favorito {

    private TipoFavorito tipo;
    private ObjectId refId;
    private ObjectId albumId;
    private String genero;
    private LocalDate fechaAgregado;

    public Favorito() {
    }

    public Favorito(TipoFavorito tipo, ObjectId refId, ObjectId albumId,
                    String genero, LocalDate fechaAgregado) {
        this.tipo = tipo;
        this.refId = refId;
        this.albumId = albumId;
        this.genero = genero;
        this.fechaAgregado = fechaAgregado;
    }

    public TipoFavorito getTipo() {
        return tipo;
    }

    public void setTipo(TipoFavorito tipo) {
        this.tipo = tipo;
    }

    public ObjectId getRefId() {
        return refId;
    }

    public void setRefId(ObjectId refId) {
        this.refId = refId;
    }

    public ObjectId getAlbumId() {
        return albumId;
    }

    public void setAlbumId(ObjectId albumId) {
        this.albumId = albumId;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public LocalDate getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDate fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}
