package com.equipo3.bibliotecamusical.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 * Artista (documento de la coleccion {@code artistas}). Un mismo tipo de clase
 * representa solistas y bandas mediante el discriminador {@link TipoArtista};
 * {@code integrantes} solo aplica cuando {@code tipo == BANDA}.
 */
public class Artista {

    private ObjectId id;
    private TipoArtista tipo;
    private String nombre;
    private String imagen;
    private String genero;
    private LocalDate fechaCreacion;
    private List<Integrante> integrantes = new ArrayList<>();

    public Artista() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public TipoArtista getTipo() {
        return tipo;
    }

    public void setTipo(TipoArtista tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Integrante> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<Integrante> integrantes) {
        this.integrantes = integrantes;
    }

    public boolean esBanda() {
        return tipo == TipoArtista.BANDA;
    }
}
