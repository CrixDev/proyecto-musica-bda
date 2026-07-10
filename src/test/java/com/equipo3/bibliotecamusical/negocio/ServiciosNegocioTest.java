package com.equipo3.bibliotecamusical.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.CancionDTO;
import com.equipo3.bibliotecamusical.dtos.CredencialesDTO;
import com.equipo3.bibliotecamusical.dtos.IntegranteDTO;
import com.equipo3.bibliotecamusical.dtos.RegistroUsuarioDTO;
import com.equipo3.bibliotecamusical.dtos.UsuarioDTO;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.negocio.excepciones.AutenticacionException;
import com.equipo3.bibliotecamusical.negocio.excepciones.DuplicadoException;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import com.equipo3.bibliotecamusical.persistencia.ConexionMongo;
import com.equipo3.bibliotecamusical.persistencia.InicializadorBd;
import com.mongodb.client.MongoDatabase;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Pruebas de la capa de negocio: validaciones, seguridad (bcrypt) y reglas CRUD. */
class ServiciosNegocioTest {

    private static final String BD_PRUEBA = "bibliotecaMusical3_test_negocio";

    private static MongoDatabase db;
    private static Servicios servicios;

    @BeforeAll
    static void preparar() {
        assumeTrue(ConexionMongo.disponible(), "MongoDB no disponible en localhost:27017");
        db = ConexionMongo.getBaseDatos(BD_PRUEBA);
        db.drop();
        InicializadorBd.inicializar(db);
        servicios = new Servicios(db);
    }

    @AfterAll
    static void limpiar() {
        if (db != null) {
            db.drop();
        }
        ConexionMongo.cerrar();
    }

    @Test
    void registroLoginYSeguridad() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO(
                "ana_negocio", "ana@bm3.com", "clave123", "clave123", null);
        UsuarioDTO creado = servicios.autenticacion().registrar(dto);
        assertEquals("ana_negocio", creado.nombreUsuario());

        UsuarioDTO login = servicios.autenticacion()
                .login(new CredencialesDTO("ana@bm3.com", "clave123"));
        assertTrue(SesionActual.hayUsuario());
        assertEquals("ana_negocio", login.nombreUsuario());

        assertThrows(AutenticacionException.class, () -> servicios.autenticacion()
                .login(new CredencialesDTO("ana_negocio", "incorrecta")));
        assertThrows(DuplicadoException.class, () -> servicios.autenticacion().registrar(dto));
    }

    @Test
    void registroRechazaCorreoInvalidoYContrasenaCorta() {
        assertThrows(ValidacionException.class, () -> servicios.autenticacion().registrar(
                new RegistroUsuarioDTO("x_user", "correo-malo", "123456", "123456", null)));
        assertThrows(ValidacionException.class, () -> servicios.autenticacion().registrar(
                new RegistroUsuarioDTO("y_user", "y@bm3.com", "123", "123", null)));
    }

    @Test
    void artistaCrudYDuplicado() {
        ArtistaDTO solista = servicios.artistas().crear(new ArtistaDTO(
                null, TipoArtista.SOLISTA, "Solista Negocio", null, "Pop", null, List.of()));
        assertEquals("Pop", solista.genero());

        // Duplicado por nombre+tipo (distinta caja)
        assertThrows(DuplicadoException.class, () -> servicios.artistas().crear(new ArtistaDTO(
                null, TipoArtista.SOLISTA, "solista negocio", null, "Rock", null, List.of())));

        // Eliminacion en cascada
        servicios.artistas().eliminar(solista.id());
        assertThrows(com.equipo3.bibliotecamusical.negocio.excepciones.NoEncontradoException.class,
                () -> servicios.artistas().obtener(solista.id()));
    }

    @Test
    void solistaPuedeAgregarseComoIntegrante() {
        // Regla nueva: un solista puede registrarse a si mismo como integrante.
        ArtistaDTO solista = servicios.artistas().crear(new ArtistaDTO(
                null, TipoArtista.SOLISTA, "Solista Con Integrante", null, "Rock", null,
                List.of(new IntegranteDTO("Solista Con Integrante", "Solista", LocalDate.of(2020, 1, 1), null, true))));
        assertEquals(1, solista.integrantes().size());
        assertEquals("Solista Con Integrante", solista.integrantes().get(0).nombreCompleto());
    }

    @Test
    void integranteActivoNoDebeTenerFechaSalida() {
        assertThrows(ValidacionException.class, () -> servicios.artistas().crear(new ArtistaDTO(
                null, TipoArtista.BANDA, "Banda Incoherente", null, "Rock", null,
                List.of(new IntegranteDTO("Mengano", "Bajo",
                        LocalDate.of(2019, 1, 1), LocalDate.of(2021, 1, 1), true)))));
    }

    @Test
    void albumRequiereAlMenosTresCancionesYGeneroValido() {
        ArtistaDTO artista = servicios.artistas().crear(new ArtistaDTO(
                null, TipoArtista.SOLISTA, "Autor Album Negocio", null, "Rock", null, List.of()));

        assertThrows(ValidacionException.class, () -> servicios.albumes().crear(new AlbumDTO(
                null, artista.id(), "Disco Corto", LocalDate.of(2020, 1, 1), "Rock", null,
                List.of(new CancionDTO(null, "A", 1, 100, "Rock"),
                        new CancionDTO(null, "B", 2, 100, "Rock")))));

        assertThrows(ValidacionException.class, () -> servicios.albumes().crear(new AlbumDTO(
                null, artista.id(), "Disco Genero Malo", LocalDate.of(2020, 1, 1), "GeneroInexistente", null,
                List.of(new CancionDTO(null, "A", 1, 100, "Rock"),
                        new CancionDTO(null, "B", 2, 100, "Rock"),
                        new CancionDTO(null, "C", 3, 100, "Rock")))));

        AlbumDTO ok = servicios.albumes().crear(new AlbumDTO(
                null, artista.id(), "Disco Valido", LocalDate.of(2020, 1, 1), "Rock", null,
                List.of(new CancionDTO(null, "A", 1, 100, "Rock"),
                        new CancionDTO(null, "B", 2, 100, "Rock"),
                        new CancionDTO(null, "C", 3, 100, "Rock"))));
        assertEquals(3, ok.canciones().size());
        assertTrue(ok.canciones().stream().allMatch(c -> c.id() != null),
                "el negocio debe generar el id de cada cancion");
    }
}
