package com.equipo3.bibliotecamusical.dtos;

import com.equipo3.bibliotecamusical.entidades.TipoFavorito;
import java.time.LocalDate;

/**
 * Favorito ya resuelto para mostrarlo en la pantalla de Favoritos: además de la
 * referencia (tipo + id) trae el nombre, subtítulo, imagen y demás datos del
 * artista/álbum/canción al que apunta, listos para pintar y navegar.
 *
 * @param tipo          artista, álbum o canción.
 * @param refId         id (hex) del artista/álbum/canción favorito.
 * @param albumId       id (hex) del álbum que contiene la canción (solo tipo CANCION).
 * @param titulo        nombre del artista/álbum/canción.
 * @param subtitulo     dato secundario (artista o género).
 * @param imagen        portada/imagen a mostrar.
 * @param duracionSegundos duración (solo canciones; 0 si no aplica).
 * @param fechaAgregado fecha en que se marcó como favorito.
 */
public record FavoritoDTO(
        TipoFavorito tipo,
        String refId,
        String albumId,
        String titulo,
        String subtitulo,
        String imagen,
        int duracionSegundos,
        LocalDate fechaAgregado) {
}
