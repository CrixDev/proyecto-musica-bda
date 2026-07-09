package com.equipo3.bibliotecamusical.persistencia;

/**
 * Un indice unico rechazo la escritura (codigo 11000 de MongoDB). La capa de
 * negocio la traduce a un mensaje de dominio (p. ej. "el correo ya existe").
 */
public class LlaveDuplicadaException extends PersistenciaException {

    public LlaveDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
