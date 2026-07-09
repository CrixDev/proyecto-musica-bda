package com.equipo3.bibliotecamusical.negocio.mapeadores;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.CancionDTO;
import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import java.util.ArrayList;
import java.util.List;

/** Conversion entre {@link Album} (dominio) y {@link AlbumDTO} (presentacion). */
public final class AlbumMapper {

    private AlbumMapper() {
    }

    public static AlbumDTO aDTO(Album a) {
        if (a == null) {
            return null;
        }
        List<CancionDTO> canciones = new ArrayList<>();
        if (a.getCanciones() != null) {
            for (Cancion c : a.getCanciones()) {
                canciones.add(new CancionDTO(
                        Ids.aHex(c.getId()), c.getNombre(),
                        c.getNumeroPista(), c.getDuracionSegundos(), c.getGenero()));
            }
        }
        return new AlbumDTO(
                Ids.aHex(a.getId()), Ids.aHex(a.getArtistaId()), a.getNombre(),
                a.getFechaLanzamiento(), a.getGenero(), a.getImagenPortada(), canciones);
    }

    public static Album aEntidad(AlbumDTO d) {
        if (d == null) {
            return null;
        }
        Album a = new Album();
        a.setId(Ids.aObjectId(d.id()));
        a.setArtistaId(Ids.aObjectId(d.artistaId()));
        a.setNombre(d.nombre());
        a.setFechaLanzamiento(d.fechaLanzamiento());
        a.setGenero(d.genero());
        a.setImagenPortada(d.imagenPortada());
        List<Cancion> canciones = new ArrayList<>();
        if (d.canciones() != null) {
            for (CancionDTO c : d.canciones()) {
                canciones.add(new Cancion(
                        Ids.aObjectId(c.id()), c.nombre(),
                        c.numeroPista(), c.duracionSegundos(), c.genero()));
            }
        }
        a.setCanciones(canciones);
        return a;
    }
}
