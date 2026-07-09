package com.equipo3.bibliotecamusical.negocio.excepciones;

/** Fallo de autenticacion (credenciales invalidas). */
public class AutenticacionException extends NegocioException {

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
