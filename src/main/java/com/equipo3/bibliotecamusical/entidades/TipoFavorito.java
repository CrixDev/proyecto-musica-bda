package com.equipo3.bibliotecamusical.entidades;

/**
 * Tipo de entidad a la que apunta un favorito. Se almacena como los tokens en
 * minusculas ("artista" / "album" / "cancion") que exige el validador de la
 * coleccion {@code usuarios}.
 */
public enum TipoFavorito implements ConClave {

    ARTISTA("artista"),
    ALBUM("album"),
    CANCION("cancion");

    private final String clave;

    TipoFavorito(String clave) {
        this.clave = clave;
    }

    @Override
    public String getClave() {
        return clave;
    }

    public static TipoFavorito desdeClave(String clave) {
        for (TipoFavorito t : values()) {
            if (t.clave.equalsIgnoreCase(clave)) {
                return t;
            }
        }
        throw new IllegalArgumentException("TipoFavorito invalido: " + clave);
    }
}
