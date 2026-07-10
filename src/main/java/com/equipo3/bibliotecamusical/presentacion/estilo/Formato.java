/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.estilo;

import com.equipo3.bibliotecamusical.dtos.CancionDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Formato de textos que se repiten en varias pantallas (duracion, fechas,
 * conteos).
 * 
 * @author Dylan
 */
public final class Formato {

    private static final DateTimeFormatter FECHA_CORTA
            = DateTimeFormatter.ofPattern("yyyy", Locale.forLanguageTag("es-MX"));
    private static final DateTimeFormatter FECHA_LARGA
            = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-MX"));

    private Formato() {
    }

    /**
     * "3:42" a partir de segundos totales.
     */
    public static String duracion(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    /**
     * Suma la duracion de una lista de canciones y la formatea como "20 min" o
     * "1h 05min".
     */
    public static String duracionTotal(List<CancionDTO> canciones) {
        int totalSegundos = canciones == null ? 0
                : canciones.stream().mapToInt(CancionDTO::duracionSegundos).sum();
        int minutos = totalSegundos / 60;
        if (minutos < 60) {
            return minutos + " min";
        }
        return String.format("%dh %02dmin", minutos / 60, minutos % 60);
    }

    /**
     * Solo el anio, para subtitulos de album ("2017").
     */
    public static String anio(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA_CORTA);
    }

    /**
     * Fecha completa en espanol, para fichas de integrantes.
     */
    public static String fechaLarga(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA_LARGA);
    }

    /**
     * "1 cancion" / "5 canciones" respetando el plural en espanol.
     */
    public static String plural(int cantidad, String singular, String plural) {
        return cantidad + " " + (cantidad == 1 ? singular : plural);
    }

    public static String canciones(int cantidad) {
        return plural(cantidad, "cancion", "canciones");
    }

    public static String albumes(int cantidad) {
        return plural(cantidad, "disco", "discos");
    }
}
