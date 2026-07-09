package com.equipo3.bibliotecamusical.negocio.validadores;

import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import com.equipo3.bibliotecamusical.entidades.Genero;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Reglas de validacion para albumes y sus canciones embebidas. */
public final class ValidadorAlbum {

    private static final int MIN_CANCIONES = 3;

    private ValidadorAlbum() {
    }

    public static void validar(Album a) {
        List<String> errores = new ArrayList<>();
        if (a == null) {
            throw new ValidacionException("Album nulo");
        }
        if (a.getArtistaId() == null) {
            errores.add("El album debe pertenecer a un artista");
        }
        if (Validaciones.vacio(a.getNombre())) {
            errores.add("El nombre del album es obligatorio");
        }
        if (Validaciones.vacio(a.getGenero())) {
            errores.add("El genero del album es obligatorio");
        } else if (!Genero.existe(a.getGenero())) {
            errores.add("Genero no valido: " + a.getGenero());
        }
        if (a.getFechaLanzamiento() == null) {
            errores.add("La fecha de lanzamiento es obligatoria");
        } else if (a.getFechaLanzamiento().isAfter(LocalDate.now())) {
            errores.add("La fecha de lanzamiento no puede ser futura");
        }
        validarCanciones(a, errores);
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }

    /** Valida una cancion individual (para altas de subdocumento). */
    public static void validarCancion(Cancion c) {
        List<String> errores = new ArrayList<>();
        validarCancion(c, errores);
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }

    private static void validarCanciones(Album a, List<String> errores) {
        List<Cancion> canciones = a.getCanciones();
        if (canciones == null || canciones.size() < MIN_CANCIONES) {
            errores.add("El album debe tener al menos " + MIN_CANCIONES + " canciones");
            return;
        }
        Set<String> nombres = new HashSet<>();
        Set<Integer> pistas = new HashSet<>();
        for (Cancion c : canciones) {
            validarCancion(c, errores);
            if (c != null && c.getNombre() != null && !nombres.add(c.getNombre().trim().toLowerCase())) {
                errores.add("Cancion duplicada en el album: " + c.getNombre());
            }
            if (c != null && !pistas.add(c.getNumeroPista())) {
                errores.add("Numero de pista repetido: " + c.getNumeroPista());
            }
        }
    }

    private static void validarCancion(Cancion c, List<String> errores) {
        if (c == null) {
            errores.add("Cancion nula");
            return;
        }
        if (Validaciones.vacio(c.getNombre())) {
            errores.add("El nombre de la cancion es obligatorio");
        }
        if (Validaciones.vacio(c.getGenero())) {
            errores.add("El genero de la cancion es obligatorio");
        } else if (!Genero.existe(c.getGenero())) {
            errores.add("Genero de cancion no valido: " + c.getGenero());
        }
        if (c.getDuracionSegundos() <= 0) {
            errores.add("La duracion de la cancion debe ser mayor a 0");
        }
        if (c.getNumeroPista() <= 0) {
            errores.add("El numero de pista debe ser mayor a 0");
        }
    }
}
