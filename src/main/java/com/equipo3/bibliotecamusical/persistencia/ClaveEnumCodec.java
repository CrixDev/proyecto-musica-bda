package com.equipo3.bibliotecamusical.persistencia;

import com.equipo3.bibliotecamusical.entidades.ConClave;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Codec generico para enums {@link ConClave}: los serializa/deserializa usando su
 * {@code clave} (token de BD) en lugar del {@code name()} de Java. Asi el
 * discriminador se guarda exactamente como lo espera el validador {@code $jsonSchema}.
 *
 * @param <T> enum que implementa {@link ConClave}
 */
public final class ClaveEnumCodec<T extends Enum<T> & ConClave> implements Codec<T> {

    private final Class<T> tipo;

    public ClaveEnumCodec(Class<T> tipo) {
        this.tipo = tipo;
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        writer.writeString(value.getClave());
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        String clave = reader.readString();
        for (T valor : tipo.getEnumConstants()) {
            if (valor.getClave().equals(clave)) {
                return valor;
            }
        }
        throw new BsonInvalidOperationException(
                "Clave desconocida para " + tipo.getSimpleName() + ": " + clave);
    }

    @Override
    public Class<T> getEncoderClass() {
        return tipo;
    }
}
