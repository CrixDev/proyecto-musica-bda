package com.equipo3.bibliotecamusical.negocio.excepciones;

import java.util.List;

/**
 * Datos invalidos segun las reglas de negocio. Puede acumular varios mensajes
 * (p. ej. todos los errores de un formulario).
 */
public class ValidacionException extends NegocioException {

    private final List<String> errores;

    public ValidacionException(String mensaje) {
        super(mensaje);
        this.errores = List.of(mensaje);
    }

    public ValidacionException(List<String> errores) {
        super(String.join("\n", errores));
        this.errores = List.copyOf(errores);
    }

    public List<String> getErrores() {
        return errores;
    }
}
