package com.equipo3.bibliotecamusical.negocio.servicios;

import com.equipo3.bibliotecamusical.daos.IAlbumDAO;
import com.equipo3.bibliotecamusical.daos.IArtistaDAO;
import com.equipo3.bibliotecamusical.daos.IUsuarioDAO;
import com.equipo3.bibliotecamusical.dtos.FavoritoDTO;
import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import com.equipo3.bibliotecamusical.entidades.Favorito;
import com.equipo3.bibliotecamusical.entidades.TipoFavorito;
import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.negocio.excepciones.NoEncontradoException;
import com.equipo3.bibliotecamusical.negocio.mapeadores.Ids;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 * Casos de uso de favoritos del usuario: marcar/desmarcar artistas, álbumes y
 * canciones, y listarlos resolviendo la referencia (refId) al dato real para
 * poder mostrarlos y navegar a ellos.
 */
public class FavoritoService {

    private final IUsuarioDAO usuarioDAO;
    private final IArtistaDAO artistaDAO;
    private final IAlbumDAO albumDAO;

    public FavoritoService(IUsuarioDAO usuarioDAO, IArtistaDAO artistaDAO, IAlbumDAO albumDAO) {
        this.usuarioDAO = usuarioDAO;
        this.artistaDAO = artistaDAO;
        this.albumDAO = albumDAO;
    }

    public boolean esFavorito(String usuarioIdHex, TipoFavorito tipo, String refIdHex) {
        ObjectId refId = Ids.aObjectId(refIdHex);
        if (refId == null) {
            return false;
        }
        Usuario u = cargar(usuarioIdHex);
        return u.getFavoritos() != null && u.getFavoritos().stream()
                .anyMatch(f -> f.getTipo() == tipo && refId.equals(f.getRefId()));
    }

    /** Marca como favorito (idempotente: si ya lo era, no hace nada). */
    public void agregar(String usuarioIdHex, TipoFavorito tipo, String refIdHex, String albumIdHex, String genero) {
        if (esFavorito(usuarioIdHex, tipo, refIdHex)) {
            return;
        }
        Favorito favorito = new Favorito(tipo, Ids.aObjectId(refIdHex),
                Ids.aObjectId(albumIdHex), genero, LocalDate.now());
        usuarioDAO.agregarFavorito(Ids.aObjectId(usuarioIdHex), favorito);
    }

    public void quitar(String usuarioIdHex, TipoFavorito tipo, String refIdHex) {
        usuarioDAO.quitarFavorito(Ids.aObjectId(usuarioIdHex), tipo, Ids.aObjectId(refIdHex));
    }

    /** Marca o desmarca según el estado deseado; devuelve el nuevo estado. */
    public boolean alternar(String usuarioIdHex, TipoFavorito tipo, String refIdHex,
            String albumIdHex, String genero, boolean deseado) {
        if (deseado) {
            agregar(usuarioIdHex, tipo, refIdHex, albumIdHex, genero);
        } else {
            quitar(usuarioIdHex, tipo, refIdHex);
        }
        return deseado;
    }

    /** Lista los favoritos del usuario ya resueltos (omite referencias que ya no existen). */
    public List<FavoritoDTO> listar(String usuarioIdHex) {
        Usuario u = cargar(usuarioIdHex);
        List<FavoritoDTO> salida = new ArrayList<>();
        if (u.getFavoritos() == null) {
            return salida;
        }
        for (Favorito f : u.getFavoritos()) {
            FavoritoDTO dto = resolver(f);
            if (dto != null) {
                salida.add(dto);
            }
        }
        return salida;
    }

    // ------------------------------------------------------------------

    private FavoritoDTO resolver(Favorito f) {
        if (f == null || f.getTipo() == null) {
            return null;
        }
        switch (f.getTipo()) {
            case ARTISTA -> {
                Artista a = artistaDAO.buscarPorId(f.getRefId()).orElse(null);
                if (a == null) {
                    return null;
                }
                return new FavoritoDTO(TipoFavorito.ARTISTA, hex(a.getId()), null,
                        a.getNombre(), a.getGenero(), a.getImagen(), 0, f.getFechaAgregado());
            }
            case ALBUM -> {
                Album al = albumDAO.buscarPorId(f.getRefId()).orElse(null);
                if (al == null) {
                    return null;
                }
                return new FavoritoDTO(TipoFavorito.ALBUM, hex(al.getId()), null,
                        al.getNombre(), nombreArtista(al.getArtistaId()),
                        al.getImagenPortada(), 0, f.getFechaAgregado());
            }
            case CANCION -> {
                Album al = albumDAO.buscarPorId(f.getAlbumId()).orElse(null);
                if (al == null || al.getCanciones() == null) {
                    return null;
                }
                Cancion c = al.getCanciones().stream()
                        .filter(x -> f.getRefId() != null && f.getRefId().equals(x.getId()))
                        .findFirst().orElse(null);
                if (c == null) {
                    return null;
                }
                String sub = nombreArtista(al.getArtistaId()) + " · " + al.getNombre();
                return new FavoritoDTO(TipoFavorito.CANCION, hex(c.getId()), hex(al.getId()),
                        c.getNombre(), sub, al.getImagenPortada(), c.getDuracionSegundos(), f.getFechaAgregado());
            }
            default -> {
                return null;
            }
        }
    }

    private String nombreArtista(ObjectId artistaId) {
        if (artistaId == null) {
            return "";
        }
        return artistaDAO.buscarPorId(artistaId).map(Artista::getNombre).orElse("");
    }

    private Usuario cargar(String usuarioIdHex) {
        ObjectId id = Ids.aObjectId(usuarioIdHex);
        return usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));
    }

    private static String hex(ObjectId id) {
        return id == null ? null : id.toHexString();
    }
}
