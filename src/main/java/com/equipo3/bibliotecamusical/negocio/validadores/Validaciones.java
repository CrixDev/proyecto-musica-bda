package com.equipo3.bibliotecamusical.negocio.validadores;

import java.util.regex.Pattern;

/** Utilidades de validacion reutilizables. */
public final class Validaciones {

    private static final Pattern CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validaciones() {
    }

    public static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    public static boolean correoValido(String s) {
        return s != null && CORREO.matcher(s.trim()).matches();
    }
}
