package com.equipo3.bibliotecamusical.negocio.mapeadores;

import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import org.bson.types.ObjectId;

/**
 * Conversion de identificadores en la frontera negocio/dto: las entidades usan
 * {@link ObjectId}; los DTOs exponen ids como {@code String} hexadecimal, de modo
 * que la presentacion nunca toca {@code ObjectId}.
 */
public final class Ids {

    private Ids() {
    }

    public static String aHex(ObjectId id) {
        return id == null ? null : id.toHexString();
    }

    public static ObjectId aObjectId(String hex) {
        if (hex == null || hex.isBlank()) {
            return null;
        }
        if (!ObjectId.isValid(hex)) {
            throw new ValidacionException("Identificador invalido: " + hex);
        }
        return new ObjectId(hex);
    }
}
