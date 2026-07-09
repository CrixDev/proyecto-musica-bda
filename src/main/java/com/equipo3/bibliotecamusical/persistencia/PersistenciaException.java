package com.equipo3.bibliotecamusical.persistencia;

/**
 * Error en la capa de acceso a datos. Envuelve las excepciones del driver de
 * MongoDB para que las capas superiores no dependan de sus tipos.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
