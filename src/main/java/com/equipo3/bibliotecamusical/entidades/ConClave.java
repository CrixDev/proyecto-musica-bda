package com.equipo3.bibliotecamusical.entidades;

/**
 * Enum cuyo valor persistido en MongoDB es una "clave" explicita (no el
 * {@code name()} de Java). Permite que los discriminadores se guarden con los
 * tokens exactos que espera el esquema {@code $jsonSchema} (p. ej. "solista"
 * en minusculas) en lugar de "SOLISTA".
 */
public interface ConClave {

    /** @return token exacto que se almacena en la base de datos. */
    String getClave();
}
