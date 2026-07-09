package com.equipo3.bibliotecamusical.negocio.servicios;

import com.equipo3.bibliotecamusical.daos.IAlbumDAO;
import com.equipo3.bibliotecamusical.daos.IArtistaDAO;
import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.CancionDTO;
import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import com.equipo3.bibliotecamusical.negocio.excepciones.DuplicadoException;
import com.equipo3.bibliotecamusical.negocio.excepciones.NoEncontradoException;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import com.equipo3.bibliotecamusical.negocio.mapeadores.AlbumMapper;
import com.equipo3.bibliotecamusical.negocio.mapeadores.Ids;
import com.equipo3.bibliotecamusical.negocio.validadores.ValidadorAlbum;
import com.equipo3.bibliotecamusical.negocio.validadores.Validaciones;
import com.equipo3.bibliotecamusical.persistencia.LlaveDuplicadaException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;

/** Casos de uso CRUD de albumes (canciones embebidas). */
public class AlbumService {

    private final IAlbumDAO albumDAO;
    private final IArtistaDAO artistaDAO;

    public AlbumService(IAlbumDAO albumDAO, IArtistaDAO artistaDAO) {
        this.albumDAO = albumDAO;
        this.artistaDAO = artistaDAO;
    }

    public AlbumDTO crear(AlbumDTO dto) {
        Album album = AlbumMapper.aEntidad(dto);
        album.setId(null);
        verificarArtistaExiste(album.getArtistaId());
        asignarIdsCanciones(album);
        ValidadorAlbum.validar(album);
        if (albumDAO.existeAlbumDeArtista(album.getArtistaId(), album.getNombre())) {
            throw new DuplicadoException("El artista ya tiene un album llamado '" + album.getNombre() + "'");
        }
        try {
            albumDAO.crear(album);
        } catch (LlaveDuplicadaException e) {
            throw new DuplicadoException("Album duplicado para ese artista");
        }
        return AlbumMapper.aDTO(album);
    }

    public AlbumDTO actualizar(AlbumDTO dto) {
        Album album = AlbumMapper.aEntidad(dto);
        if (album.getId() == null) {
            throw new ValidacionException("Falta el id del album a actualizar");
        }
        cargar(album.getId());
        verificarArtistaExiste(album.getArtistaId());
        asignarIdsCanciones(album);
        ValidadorAlbum.validar(album);
        try {
            albumDAO.actualizar(album);
        } catch (LlaveDuplicadaException e) {
            throw new DuplicadoException("Ya existe otro album con ese nombre para el artista");
        }
        return AlbumMapper.aDTO(album);
    }

    public void eliminar(String idHex) {
        ObjectId id = Ids.aObjectId(idHex);
        cargar(id);
        albumDAO.eliminar(id);
    }

    public AlbumDTO obtener(String idHex) {
        return AlbumMapper.aDTO(cargar(Ids.aObjectId(idHex)));
    }

    public List<AlbumDTO> listar() {
        return mapear(albumDAO.listar());
    }

    public List<AlbumDTO> listarPorArtista(String artistaIdHex) {
        return mapear(albumDAO.listarPorArtista(Ids.aObjectId(artistaIdHex)));
    }

    public List<AlbumDTO> buscarPorNombre(String texto) {
        if (Validaciones.vacio(texto)) {
            return listar();
        }
        return mapear(albumDAO.buscarPorNombre(texto.trim()));
    }

    // --- Canciones (subdocumentos) ---

    public void agregarCancion(String albumIdHex, CancionDTO dto) {
        ObjectId albumId = Ids.aObjectId(albumIdHex);
        Album album = cargar(albumId);
        Cancion cancion = new Cancion(
                new ObjectId(), dto.nombre(), dto.numeroPista(), dto.duracionSegundos(), dto.genero());
        ValidadorAlbum.validarCancion(cancion);
        boolean duplicada = album.getCanciones().stream().anyMatch(c ->
                c.getNombre().equalsIgnoreCase(cancion.getNombre())
                        || c.getNumeroPista() == cancion.getNumeroPista());
        if (duplicada) {
            throw new DuplicadoException("Ya existe una cancion con ese nombre o numero de pista en el album");
        }
        albumDAO.agregarCancion(albumId, cancion);
    }

    public void eliminarCancion(String albumIdHex, String cancionIdHex) {
        Album album = cargar(Ids.aObjectId(albumIdHex));
        if (album.getCanciones().size() <= 3) {
            throw new ValidacionException("El album debe conservar al menos 3 canciones");
        }
        albumDAO.eliminarCancion(album.getId(), Ids.aObjectId(cancionIdHex));
    }

    private void verificarArtistaExiste(ObjectId artistaId) {
        if (artistaId == null || artistaDAO.buscarPorId(artistaId).isEmpty()) {
            throw new NoEncontradoException("El artista del album no existe");
        }
    }

    private void asignarIdsCanciones(Album album) {
        if (album.getCanciones() == null) {
            return;
        }
        for (Cancion c : album.getCanciones()) {
            if (c.getId() == null) {
                c.setId(new ObjectId());
            }
        }
    }

    private Album cargar(ObjectId id) {
        Optional<Album> a = albumDAO.buscarPorId(id);
        return a.orElseThrow(() -> new NoEncontradoException("Album no encontrado"));
    }

    private List<AlbumDTO> mapear(List<Album> albumes) {
        return albumes.stream().map(AlbumMapper::aDTO).collect(Collectors.toList());
    }
}
