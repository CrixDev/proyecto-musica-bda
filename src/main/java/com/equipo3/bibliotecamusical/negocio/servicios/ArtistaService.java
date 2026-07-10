package com.equipo3.bibliotecamusical.negocio.servicios;

import com.equipo3.bibliotecamusical.daos.IAlbumDAO;
import com.equipo3.bibliotecamusical.daos.IArtistaDAO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.IntegranteDTO;
import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Integrante;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.negocio.excepciones.DuplicadoException;
import com.equipo3.bibliotecamusical.negocio.excepciones.NoEncontradoException;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import com.equipo3.bibliotecamusical.negocio.mapeadores.ArtistaMapper;
import com.equipo3.bibliotecamusical.negocio.mapeadores.Ids;
import com.equipo3.bibliotecamusical.negocio.validadores.ValidadorArtista;
import com.equipo3.bibliotecamusical.negocio.validadores.Validaciones;
import com.equipo3.bibliotecamusical.persistencia.LlaveDuplicadaException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;

/** Casos de uso CRUD de artistas (incluye integrantes embebidos y cascada a albumes). */
public class ArtistaService {

    private final IArtistaDAO artistaDAO;
    private final IAlbumDAO albumDAO;

    public ArtistaService(IArtistaDAO artistaDAO, IAlbumDAO albumDAO) {
        this.artistaDAO = artistaDAO;
        this.albumDAO = albumDAO;
    }

    public ArtistaDTO crear(ArtistaDTO dto) {
        Artista artista = ArtistaMapper.aEntidad(dto);
        artista.setId(null);
        if (artista.getFechaCreacion() == null) {
            artista.setFechaCreacion(LocalDate.now());
        }
        ValidadorArtista.validar(artista);
        if (artistaDAO.existePorNombreYTipo(artista.getNombre(), artista.getTipo())) {
            throw new DuplicadoException("Ya existe un " + artista.getTipo().getClave()
                    + " con el nombre '" + artista.getNombre() + "'");
        }
        try {
            artistaDAO.crear(artista);
        } catch (LlaveDuplicadaException e) {
            throw new DuplicadoException("Artista duplicado");
        }
        return ArtistaMapper.aDTO(artista);
    }

    public ArtistaDTO actualizar(ArtistaDTO dto) {
        Artista artista = ArtistaMapper.aEntidad(dto);
        if (artista.getId() == null) {
            throw new ValidacionException("Falta el id del artista a actualizar");
        }
        cargar(artista.getId());
        ValidadorArtista.validar(artista);
        try {
            artistaDAO.actualizar(artista);
        } catch (LlaveDuplicadaException e) {
            throw new DuplicadoException("Ya existe otro artista con ese nombre y tipo");
        }
        return ArtistaMapper.aDTO(artista);
    }

    public void eliminar(String idHex) {
        ObjectId id = Ids.aObjectId(idHex);
        cargar(id);
        // Cascada: primero los albumes del artista (servidor standalone, sin transacciones).
        albumDAO.eliminarPorArtista(id);
        artistaDAO.eliminar(id);
    }

    public ArtistaDTO obtener(String idHex) {
        return ArtistaMapper.aDTO(cargar(Ids.aObjectId(idHex)));
    }

    public List<ArtistaDTO> listar() {
        return mapear(artistaDAO.listar());
    }

    public List<ArtistaDTO> listarPorTipo(TipoArtista tipo) {
        return mapear(artistaDAO.listarPorTipo(tipo));
    }

    public List<ArtistaDTO> listarPorGenero(String genero) {
        return mapear(artistaDAO.listarPorGenero(genero));
    }

    public List<ArtistaDTO> buscarPorNombre(String texto) {
        if (Validaciones.vacio(texto)) {
            return listar();
        }
        return mapear(artistaDAO.buscarPorNombre(texto.trim()));
    }

    public long contar() {
        return artistaDAO.contar();
    }

    // --- Integrantes (subdocumentos) ---

    public void agregarIntegrante(String artistaIdHex, IntegranteDTO dto) {
        ObjectId id = Ids.aObjectId(artistaIdHex);
        Artista artista = cargar(id);
        if (!artista.esBanda()) {
            throw new ValidacionException("Solo las bandas pueden tener integrantes");
        }
        Integrante integrante = new Integrante(
                dto.nombreCompleto(), dto.rol(), dto.fechaIngreso(), dto.fechaSalida(), dto.activo());
        ValidadorArtista.validarIntegrante(integrante);
        artistaDAO.agregarIntegrante(id, integrante);
    }

    public void eliminarIntegrante(String artistaIdHex, String nombreCompleto) {
        artistaDAO.eliminarIntegrante(Ids.aObjectId(artistaIdHex), nombreCompleto);
    }

    private Artista cargar(ObjectId id) {
        Optional<Artista> a = artistaDAO.buscarPorId(id);
        return a.orElseThrow(() -> new NoEncontradoException("Artista no encontrado"));
    }

    private List<ArtistaDTO> mapear(List<Artista> artistas) {
        return artistas.stream().map(ArtistaMapper::aDTO).collect(Collectors.toList());
    }
    
    /**
     * Inserta un lote de entidades Artista previamente validadas.
     */
    public void insertarLoteArtistas(List<Artista> artistas) {
        if (artistas == null || artistas.isEmpty()) {
            throw new ValidacionException("La lista de artistas está vacía");
        }

        // Regla: 30 registros estrictos de tu parte
        if (artistas.size() < 30) {
            throw new ValidacionException("Se requieren exactamente 30 registros. Recibidos: " + artistas.size());
        }

        // Regla: Mitad y mitad
        long countSolistas = artistas.stream().filter(a -> a.getTipo() == TipoArtista.SOLISTA).count();
        long countBandas = artistas.stream().filter(a -> a.getTipo() == TipoArtista.BANDA).count();

        if (countSolistas < 15 || countBandas < 15) {
            throw new ValidacionException("La inserción debe ser 15 solistas y 15 bandas.");
        }

        // Validar reglas internas y duplicados antes de tocar la BD
        for (Artista artista : artistas) {
            ValidadorArtista.validar(artista);
            if (artistaDAO.existePorNombreYTipo(artista.getNombre(), artista.getTipo())) {
                throw new DuplicadoException("Ya existe el artista '" + artista.getNombre() + "'");
            }
        }

        // Inserción masiva
        try {
            artistaDAO.crearMuchos(artistas);
        } catch (LlaveDuplicadaException e) {
            throw new DuplicadoException("Error de duplicados al registrar el lote.");
        }
    }
}
