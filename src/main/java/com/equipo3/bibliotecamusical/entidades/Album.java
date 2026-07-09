package com.equipo3.bibliotecamusical.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 * Album (documento de la coleccion {@code albumes}). Se relaciona con su artista
 * por <em>referencia</em> ({@code artistaId}) y embebe sus {@link Cancion canciones}.
 */
public class Album {

    private ObjectId id;
    private ObjectId artistaId;
    private String nombre;
    private LocalDate fechaLanzamiento;
    private String genero;
    private String imagenPortada;
    private List<Cancion> canciones = new ArrayList<>();

    public Album() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getArtistaId() {
        return artistaId;
    }

    public void setArtistaId(ObjectId artistaId) {
        this.artistaId = artistaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getImagenPortada() {
        return imagenPortada;
    }

    public void setImagenPortada(String imagenPortada) {
        this.imagenPortada = imagenPortada;
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<Cancion> canciones) {
        this.canciones = canciones;
    }
}
