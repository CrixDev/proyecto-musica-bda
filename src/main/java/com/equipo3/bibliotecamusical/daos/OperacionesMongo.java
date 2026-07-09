package com.equipo3.bibliotecamusical.daos;

import com.equipo3.bibliotecamusical.persistencia.LlaveDuplicadaException;
import com.equipo3.bibliotecamusical.persistencia.PersistenciaException;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import java.util.function.Supplier;

/**
 * Envuelve las operaciones del driver para traducir sus excepciones a las de la
 * capa de persistencia (asi las capas superiores no dependen de tipos de Mongo).
 * El codigo 11000 (llave duplicada) se traduce a {@link LlaveDuplicadaException}.
 */
final class OperacionesMongo {

    private static final int LLAVE_DUPLICADA = 11000;

    private OperacionesMongo() {
    }

    static <T> T ejecutar(Supplier<T> operacion) {
        try {
            return operacion.get();
        } catch (MongoWriteException e) {
            if (e.getError().getCode() == LLAVE_DUPLICADA) {
                throw new LlaveDuplicadaException("Registro duplicado", e);
            }
            throw new PersistenciaException("Error de escritura en MongoDB: " + e.getError(), e);
        } catch (MongoBulkWriteException e) {
            boolean duplicado = e.getWriteErrors().stream()
                    .anyMatch(we -> we.getCode() == LLAVE_DUPLICADA);
            if (duplicado) {
                throw new LlaveDuplicadaException("Registro(s) duplicado(s) en insercion masiva", e);
            }
            throw new PersistenciaException("Error en insercion masiva: " + e.getWriteErrors(), e);
        } catch (MongoException e) {
            throw new PersistenciaException("Error de MongoDB", e);
        }
    }

    static void ejecutar(Runnable operacion) {
        ejecutar(() -> {
            operacion.run();
            return null;
        });
    }
}
