package com.equipo3.bibliotecamusical.negocio.excepciones;

/** No existe la entidad solicitada. */
public class NoEncontradoException extends NegocioException {

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
