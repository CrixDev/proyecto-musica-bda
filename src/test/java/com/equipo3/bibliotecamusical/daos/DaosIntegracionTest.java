package com.equipo3.bibliotecamusical.daos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import com.equipo3.bibliotecamusical.entidades.Integrante;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.persistencia.ConexionMongo;
import com.equipo3.bibliotecamusical.persistencia.InicializadorBd;
import com.equipo3.bibliotecamusical.persistencia.LlaveDuplicadaException;
import com.equipo3.bibliotecamusical.persistencia.PersistenciaException;
import com.mongodb.client.MongoDatabase;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de integracion de la capa DAO contra un MongoDB local. Usan una base
 * de datos aislada ({@code bibliotecaMusical3_test}) que se limpia al terminar.
 * Si no hay MongoDB disponible, las pruebas se omiten (no fallan).
 */
class DaosIntegracionTest {

    private static final String BD_PRUEBA = "bibliotecaMusical3_test";

    private static MongoDatabase db;
    private static IArtistaDAO artistaDAO;
    private static IAlbumDAO albumDAO;
    private static IUsuarioDAO usuarioDAO;

    @BeforeAll
    static void preparar() {
        assumeTrue(ConexionMongo.disponible(), "MongoDB no disponible en localhost:27017");
        db = ConexionMongo.getBaseDatos(BD_PRUEBA);
        db.drop();
        InicializadorBd.inicializar(db);
        artistaDAO = new ArtistaDAOImpl(db);
        albumDAO = new AlbumDAOImpl(db);
        usuarioDAO = new UsuarioDAOImpl(db);
    }

    @AfterAll
    static void limpiar() {
        if (db != null) {
            db.drop();
        }
        ConexionMongo.cerrar();
    }

    @Test
    void crearYBuscarArtistaBandaConIntegrantes() {
        Artista banda = nuevaBanda("Los Prueba Alfa", "Rock");
        artistaDAO.crear(banda);

        assertNotNull(banda.getId(), "el id debe generarse al insertar");

        Optional<Artista> encontrado = artistaDAO.buscarPorId(banda.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(TipoArtista.BANDA, encontrado.get().getTipo());
        assertEquals(2, encontrado.get().getIntegrantes().size());
        assertEquals(LocalDate.of(2020, 1, 15), encontrado.get().getFechaCreacion(),
                "la fecha (LocalDate) debe conservar su valor al ir/volver de BSON date");
    }

    @Test
    void unicidadArtistaPorNombreYTipoIgnorandoMayusculas() {
        artistaDAO.crear(nuevoSolista("Duplicado Uno", "Pop"));
        // Mismo nombre (distinta caja) y mismo tipo debe violar el indice unico con collation.
        assertThrows(LlaveDuplicadaException.class,
                () -> artistaDAO.crear(nuevoSolista("duplicado uno", "Jazz")));
    }

    @Test
    void crearAlbumConCancionesEmbebidas() {
        Artista artista = nuevoSolista("Autor Album", "Pop");
        artistaDAO.crear(artista);

        Album album = nuevoAlbum(artista.getId(), "Primer Disco", "Pop", 3);
        albumDAO.crear(album);
        assertNotNull(album.getId());

        Optional<Album> encontrado = albumDAO.buscarPorId(album.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(3, encontrado.get().getCanciones().size());
        Cancion primera = encontrado.get().getCanciones().get(0);
        assertNotNull(primera.getId(), "cada cancion debe tener su _id embebido");
        assertNotNull(primera.getGenero(), "cada cancion debe tener genero");
    }

    @Test
    void validadorRechazaAlbumConMenosDeTresCanciones() {
        Artista artista = nuevoSolista("Autor Invalido", "Rock");
        artistaDAO.crear(artista);
        Album album = nuevoAlbum(artista.getId(), "Disco Corto", "Rock", 2);
        // El $jsonSchema exige minItems: 3 en canciones.
        assertThrows(PersistenciaException.class, () -> albumDAO.crear(album));
    }

    @Test
    void subdocumentosCancionPushYPull() {
        Artista artista = nuevoSolista("Autor Subdocs", "Rock");
        artistaDAO.crear(artista);
        Album album = nuevoAlbum(artista.getId(), "Disco Subdocs", "Rock", 3);
        albumDAO.crear(album);

        Cancion extra = new Cancion(new ObjectId(), "Bonus Track", 4, 200, "Rock");
        albumDAO.agregarCancion(album.getId(), extra);
        assertEquals(4, albumDAO.buscarPorId(album.getId()).orElseThrow().getCanciones().size());

        albumDAO.eliminarCancion(album.getId(), extra.getId());
        assertEquals(3, albumDAO.buscarPorId(album.getId()).orElseThrow().getCanciones().size());
    }

    @Test
    void eliminarPorArtistaBorraSusAlbumes() {
        Artista artista = nuevoSolista("Autor Cascada", "Metal");
        artistaDAO.crear(artista);
        albumDAO.crear(nuevoAlbum(artista.getId(), "Cascada 1", "Metal", 3));
        albumDAO.crear(nuevoAlbum(artista.getId(), "Cascada 2", "Metal", 3));

        assertEquals(2, albumDAO.contarPorArtista(artista.getId()));
        long borrados = albumDAO.eliminarPorArtista(artista.getId());
        assertEquals(2, borrados);
        assertEquals(0, albumDAO.contarPorArtista(artista.getId()));
    }

    @Test
    void usuarioCrearBuscarYUnicidad() {
        Usuario u = new Usuario();
        u.setNombreUsuario("prueba_user");
        u.setCorreo("prueba_user@bm3.com");
        u.setContrasena("$2a$hashfalso");
        u.setFechaRegistro(LocalDate.now());
        usuarioDAO.crear(u);

        assertTrue(usuarioDAO.existeNombreUsuario("prueba_user"));
        assertTrue(usuarioDAO.buscarPorNombreUsuarioOCorreo("prueba_user@bm3.com").isPresent());

        Usuario repetido = new Usuario();
        repetido.setNombreUsuario("prueba_user");
        repetido.setCorreo("otro@bm3.com");
        repetido.setContrasena("x");
        repetido.setFechaRegistro(LocalDate.now());
        assertThrows(LlaveDuplicadaException.class, () -> usuarioDAO.crear(repetido));
    }

    @Test
    void busquedaPorNombreParcialEscapada() {
        artistaDAO.crear(nuevoSolista("Estrella Fugaz", "Pop"));
        List<Artista> res = artistaDAO.buscarPorNombre("estrella");
        assertFalse(res.isEmpty(), "la busqueda parcial insensible a mayusculas debe encontrarlo");
    }

    // ------------------------------------------------------------------
    // Ayudas
    // ------------------------------------------------------------------

    private static Artista nuevoSolista(String nombre, String genero) {
        Artista a = new Artista();
        a.setTipo(TipoArtista.SOLISTA);
        a.setNombre(nombre);
        a.setGenero(genero);
        a.setFechaCreacion(LocalDate.of(2021, 6, 1));
        a.setIntegrantes(new ArrayList<>());
        return a;
    }

    private static Artista nuevaBanda(String nombre, String genero) {
        Artista a = new Artista();
        a.setTipo(TipoArtista.BANDA);
        a.setNombre(nombre);
        a.setGenero(genero);
        a.setFechaCreacion(LocalDate.of(2020, 1, 15));
        List<Integrante> integrantes = new ArrayList<>();
        integrantes.add(new Integrante("Ana Vocalista", "Voz", LocalDate.of(2020, 1, 15), null, true));
        integrantes.add(new Integrante("Beto Bajo", "Bajo", LocalDate.of(2020, 1, 15),
                LocalDate.of(2022, 3, 10), false));
        a.setIntegrantes(integrantes);
        return a;
    }

    private static Album nuevoAlbum(ObjectId artistaId, String nombre, String genero, int numCanciones) {
        Album album = new Album();
        album.setArtistaId(artistaId);
        album.setNombre(nombre);
        album.setGenero(genero);
        album.setFechaLanzamiento(LocalDate.of(2021, 9, 5));
        List<Cancion> canciones = new ArrayList<>();
        for (int i = 1; i <= numCanciones; i++) {
            canciones.add(new Cancion(new ObjectId(), "Pista " + i, i, 180 + i, genero));
        }
        album.setCanciones(canciones);
        return album;
    }
}
