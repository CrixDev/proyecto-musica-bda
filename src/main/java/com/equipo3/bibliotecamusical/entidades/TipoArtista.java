package com.equipo3.bibliotecamusical.entidades;

/**
 * Discriminador del tipo de artista. Se almacena como los tokens en minusculas
 * ("solista" / "banda") que exige el validador de la coleccion {@code artistas}.
 */
public enum TipoArtista implements ConClave {

    SOLISTA("solista"),
    BANDA("banda");

    private final String clave;

    TipoArtista(String clave) {
        this.clave = clave;
    }

    @Override
    public String getClave() {
        return clave;
    }

    public static TipoArtista desdeClave(String clave) {
        for (TipoArtista t : values()) {
            if (t.clave.equalsIgnoreCase(clave)) {
                return t;
            }
        }
        throw new IllegalArgumentException("TipoArtista invalido: " + clave);
    }
}
