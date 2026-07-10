package com.equipo3.bibliotecamusical.entidades;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Catalogo controlado de generos musicales. En la base de datos el genero se
 * guarda como {@code String} (asi lo define el esquema), pero este catalogo evita
 * duplicados semanticos ("Hip-Hop" vs "hip hop" vs "HipHop") y sostiene los
 * filtros, restricciones y busquedas. Los validadores comprueban que un genero
 * pertenezca a este catalogo.
 */
public enum Genero {

    ROCK("Rock"),
    POP("Pop"),
    HIP_HOP("Hip-Hop"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    METAL("Metal"),
    PUNK("Punk"),
    REGGAE("Reggae"),
    REGGAETON("Reggaeton"),
    ELECTRONICA("Electronica"),
    CLASICA("Clasica"),
    COUNTRY("Country"),
    FOLK("Folk"),
    RNB("R&B"),
    SOUL("Soul"),
    INDIE("Indie"),
    SALSA("Salsa"),
    CUMBIA("Cumbia"),
    BALADA("Balada"),
    CORRIDOS("Corridos");
    
    private final String nombre;

    Genero(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    /** @return true si el nombre coincide (ignorando mayusculas) con algun genero del catalogo. */
    public static boolean existe(String nombre) {
        if (nombre == null) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(g -> g.nombre.equalsIgnoreCase(nombre.trim()));
    }

    /** @return el genero del catalogo cuyo nombre coincide, o null si no existe. */
    public static Genero desdeNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(g -> g.nombre.equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElse(null);
    }

    /** @return lista de nombres del catalogo, util para poblar combos en la UI. */
    public static List<String> nombres() {
        return Arrays.stream(values()).map(Genero::getNombre).collect(Collectors.toList());
    }
}
