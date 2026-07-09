package com.equipo3.bibliotecamusical.negocio.excepciones;

/** Se intento crear una entidad que viola una restriccion de unicidad. */
public class DuplicadoException extends NegocioException {

    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}
