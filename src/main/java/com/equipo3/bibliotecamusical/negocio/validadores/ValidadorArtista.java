package com.equipo3.bibliotecamusical.negocio.validadores;

import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Genero;
import com.equipo3.bibliotecamusical.entidades.Integrante;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Reglas de validacion para artistas (solistas y bandas) e integrantes. */
public final class ValidadorArtista {

    private ValidadorArtista() {
    }

    public static void validar(Artista a) {
        List<String> errores = new ArrayList<>();
        if (a == null) {
            throw new ValidacionException("Artista nulo");
        }
        if (Validaciones.vacio(a.getNombre())) {
            errores.add("El nombre del artista es obligatorio");
        }
        if (a.getTipo() == null) {
            errores.add("El tipo de artista (solista/banda) es obligatorio");
        }
        if (Validaciones.vacio(a.getGenero())) {
            errores.add("El genero es obligatorio");
        } else if (!Genero.existe(a.getGenero())) {
            errores.add("Genero no valido: " + a.getGenero());
        }
        if (a.getFechaCreacion() != null && a.getFechaCreacion().isAfter(LocalDate.now())) {
            errores.add("La fecha de creacion no puede ser futura");
        }
        validarIntegrantes(a, errores);
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }

    /** Valida un integrante individual (para altas de subdocumento). */
    public static void validarIntegrante(Integrante i) {
        List<String> errores = new ArrayList<>();
        validarIntegrante(i, errores);
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }

    private static void validarIntegrantes(Artista a, List<String> errores) {
        boolean tieneIntegrantes = a.getIntegrantes() != null && !a.getIntegrantes().isEmpty();
        // Una banda debe tener al menos un integrante. Un solista puede registrarse
        // a si mismo como integrante (se agrega a el mismo), asi que tambien se permite.
        if (a.esBanda() && !tieneIntegrantes) {
            errores.add("Una banda debe tener al menos un integrante");
        }
        if (tieneIntegrantes) {
            for (Integrante i : a.getIntegrantes()) {
                validarIntegrante(i, errores);
            }
        }
    }

    private static void validarIntegrante(Integrante i, List<String> errores) {
        if (i == null) {
            errores.add("Integrante nulo");
            return;
        }
        if (Validaciones.vacio(i.getNombreCompleto())) {
            errores.add("El nombre del integrante es obligatorio");
        }
        if (Validaciones.vacio(i.getRol())) {
            errores.add("El rol del integrante es obligatorio");
        }
        if (i.getFechaIngreso() == null) {
            errores.add("La fecha de ingreso del integrante es obligatoria");
        } else if (i.getFechaIngreso().isAfter(LocalDate.now())) {
            errores.add("La fecha de ingreso no puede ser futura");
        }
        if (i.getFechaSalida() != null && i.getFechaIngreso() != null
                && !i.getFechaSalida().isAfter(i.getFechaIngreso())) {
            errores.add("La fecha de salida debe ser posterior a la de ingreso");
        }
        // Invariante: activo <=> sin fecha de salida
        boolean coherente = i.isActivo() == (i.getFechaSalida() == null);
        if (!coherente) {
            errores.add("Incoherencia en '" + i.getNombreCompleto()
                    + "': un integrante activo no debe tener fecha de salida y viceversa");
        }
    }
}
