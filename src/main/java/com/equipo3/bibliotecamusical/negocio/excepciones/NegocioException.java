package com.equipo3.bibliotecamusical.negocio.excepciones;

/** Excepcion base de la capa de negocio. La UI la captura para mostrar mensajes. */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }

    public NegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
