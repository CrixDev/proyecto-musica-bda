package com.equipo3.bibliotecamusical.negocio.mapeadores;

import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.IntegranteDTO;
import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Integrante;
import java.util.ArrayList;
import java.util.List;

/** Conversion entre {@link Artista} (dominio) y {@link ArtistaDTO} (presentacion). */
public final class ArtistaMapper {

    private ArtistaMapper() {
    }

    public static ArtistaDTO aDTO(Artista a) {
        if (a == null) {
            return null;
        }
        List<IntegranteDTO> integrantes = new ArrayList<>();
        if (a.getIntegrantes() != null) {
            for (Integrante i : a.getIntegrantes()) {
                integrantes.add(new IntegranteDTO(
                        i.getNombreCompleto(), i.getRol(),
                        i.getFechaIngreso(), i.getFechaSalida(), i.isActivo()));
            }
        }
        return new ArtistaDTO(
                Ids.aHex(a.getId()), a.getTipo(), a.getNombre(),
                a.getImagen(), a.getGenero(), a.getFechaCreacion(), integrantes);
    }

    public static Artista aEntidad(ArtistaDTO d) {
        if (d == null) {
            return null;
        }
        Artista a = new Artista();
        a.setId(Ids.aObjectId(d.id()));
        a.setTipo(d.tipo());
        a.setNombre(d.nombre());
        a.setImagen(d.imagen());
        a.setGenero(d.genero());
        a.setFechaCreacion(d.fechaCreacion());
        List<Integrante> integrantes = new ArrayList<>();
        if (d.integrantes() != null) {
            for (IntegranteDTO i : d.integrantes()) {
                integrantes.add(new Integrante(
                        i.nombreCompleto(), i.rol(),
                        i.fechaIngreso(), i.fechaSalida(), i.activo()));
            }
        }
        a.setIntegrantes(integrantes);
        return a;
    }
}
