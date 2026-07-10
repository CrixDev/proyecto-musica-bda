package com.equipo3.bibliotecamusical.daos;

import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Integrante;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

/** Repositorio de artistas (coleccion {@code artistas}). */
public interface IArtistaDAO {

    Artista crear(Artista artista);

    Optional<Artista> buscarPorId(ObjectId id);

    List<Artista> listar();

    List<Artista> listarPorTipo(TipoArtista tipo);

    List<Artista> listarPorGenero(String genero);

    /** Busqueda parcial e insensible a mayusculas por nombre. */
    List<Artista> buscarPorNombre(String texto);

    boolean existePorNombreYTipo(String nombre, TipoArtista tipo);

    Artista actualizar(Artista artista);

    boolean eliminar(ObjectId id);

    long contar();

    // --- Subdocumentos: integrantes ---

    void agregarIntegrante(ObjectId artistaId, Integrante integrante);

    void eliminarIntegrante(ObjectId artistaId, String nombreCompleto);
    
    void crearMuchos(List<Artista> artistas);
}
