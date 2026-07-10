package com.equipo3.bibliotecamusical.daos;

import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

/** Repositorio de albumes (coleccion {@code albumes}). Las canciones van embebidas. */
public interface IAlbumDAO {

    Album crear(Album album);

    /** Insercion masiva no ordenada (para la carga de datos de prueba). */
    void insertarVarios(List<Album> albumes);

    Optional<Album> buscarPorId(ObjectId id);

    List<Album> listar();

    List<Album> listarPorArtista(ObjectId artistaId);

    List<Album> listarPorGenero(String genero);

    List<Album> buscarPorNombre(String texto);

    /**
     * Busqueda parcial e insensible a mayusculas que coincide con el nombre del
     * album o con el nombre de alguna de sus canciones embebidas.
     */
    List<Album> buscarPorTexto(String texto);

    boolean existeAlbumDeArtista(ObjectId artistaId, String nombre);

    long contarPorArtista(ObjectId artistaId);

    Album actualizar(Album album);

    boolean eliminar(ObjectId id);

    /** Elimina todos los albumes de un artista (cascada al borrar el artista). */
    long eliminarPorArtista(ObjectId artistaId);

    // --- Subdocumentos: canciones ---

    void agregarCancion(ObjectId albumId, Cancion cancion);

    void eliminarCancion(ObjectId albumId, ObjectId cancionId);
}
