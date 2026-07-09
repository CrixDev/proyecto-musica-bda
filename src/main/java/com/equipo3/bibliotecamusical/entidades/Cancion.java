package com.equipo3.bibliotecamusical.entidades;

import org.bson.types.ObjectId;

/**
 * Cancion embebida dentro de un {@link Album}. Tiene {@code _id} propio (generado
 * por la aplicacion, no automatico al ser subdocumento) para poder referenciarla
 * desde los favoritos de un usuario.
 */
public class Cancion {

    private ObjectId id;
    private String nombre;
    private int numeroPista;
    private int duracionSegundos;
    private String genero;

    public Cancion() {
    }

    public Cancion(ObjectId id, String nombre, int numeroPista, int duracionSegundos, String genero) {
        this.id = id;
        this.nombre = nombre;
        this.numeroPista = numeroPista;
        this.duracionSegundos = duracionSegundos;
        this.genero = genero;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumeroPista() {
        return numeroPista;
    }

    public void setNumeroPista(int numeroPista) {
        this.numeroPista = numeroPista;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(int duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
