package com.equipo3.bibliotecamusical.presentacion;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.CancionDTO;
import com.equipo3.bibliotecamusical.dtos.CredencialesDTO;
import com.equipo3.bibliotecamusical.dtos.IntegranteDTO;
import com.equipo3.bibliotecamusical.dtos.RegistroUsuarioDTO;
import com.equipo3.bibliotecamusical.dtos.UsuarioDTO;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.persistencia.ConexionMongo;
import com.equipo3.bibliotecamusical.persistencia.InicializadorBd;
import java.time.LocalDate;
import java.util.List;

/**
 * Demostracion por consola del CRUD backend (Fases 1-6), SIN interfaz grafica.
 * Ejercita: registro+login (bcrypt), CRUD de artista con albumes y canciones,
 * subdocumentos (integrantes) y borrado en cascada. Limpia lo que crea.
 */
public final class RunnerCrud {

    private RunnerCrud() {
    }

    public static void main(String[] args) {
        if (!ConexionMongo.disponible()) {
            System.out.println("MongoDB no esta disponible en localhost:27017. Inicia el servidor y reintenta.");
            return;
        }
        InicializadorBd.inicializar(ConexionMongo.getBaseDatos());
        Servicios servicios = new Servicios(ConexionMongo.getBaseDatos());

        String sufijo = Long.toString(System.nanoTime());
        String nombreUsuario = "demo_" + sufijo;
        String nombreArtista = "Banda Demo " + sufijo;

        System.out.println("=== DEMO CRUD Biblioteca Musical (Equipo 3) ===\n");

        // 1) Registro + login
        UsuarioDTO usuario = servicios.autenticacion().registrar(new RegistroUsuarioDTO(
                nombreUsuario, nombreUsuario + "@bm3.com", "secreta123", "secreta123", null));
        System.out.println("[OK] Usuario registrado: " + usuario.nombreUsuario() + " (id=" + usuario.id() + ")");
        UsuarioDTO logueado = servicios.autenticacion().login(
                new CredencialesDTO(nombreUsuario, "secreta123"));
        System.out.println("[OK] Login correcto (bcrypt) para: " + logueado.correo());

        // 2) Crear artista (banda) con integrantes
        ArtistaDTO banda = servicios.artistas().crear(new ArtistaDTO(
                null, TipoArtista.BANDA, nombreArtista, null, "Rock", LocalDate.of(2015, 3, 1),
                List.of(new IntegranteDTO("Ana Ruiz", "Voz", LocalDate.of(2015, 3, 1), null, true))));
        System.out.println("[OK] Artista creado: " + banda.nombre() + " (id=" + banda.id() + ")");

        // 3) Crear 2 albumes con 3 canciones cada uno
        for (int n = 1; n <= 2; n++) {
            AlbumDTO album = servicios.albumes().crear(new AlbumDTO(
                    null, banda.id(), "Disco " + n + " " + sufijo, LocalDate.of(2016, 5, n), "Rock", null,
                    List.of(
                            new CancionDTO(null, "Tema A" + n, 1, 200, "Rock"),
                            new CancionDTO(null, "Tema B" + n, 2, 210, "Rock"),
                            new CancionDTO(null, "Tema C" + n, 3, 220, "Rock"))));
            System.out.println("    [OK] Album '" + album.nombre() + "' con "
                    + album.canciones().size() + " canciones");
        }

        // 4) Consultas
        List<AlbumDTO> albumes = servicios.albumes().listarPorArtista(banda.id());
        System.out.println("[OK] El artista tiene " + albumes.size() + " albumes");

        // 5) Actualizar artista (agregar integrante y cambiar genero)
        servicios.artistas().agregarIntegrante(banda.id(),
                new IntegranteDTO("Beto Diaz", "Bateria", LocalDate.of(2017, 1, 1), null, true));
        ArtistaDTO recargado = servicios.artistas().obtener(banda.id());
        System.out.println("[OK] Integrantes tras alta: " + recargado.integrantes().size());

        // 6) Eliminar artista (cascada a sus albumes)
        servicios.artistas().eliminar(banda.id());
        System.out.println("[OK] Artista eliminado; albumes restantes: "
                + servicios.albumes().listarPorArtista(banda.id()).size());

        // 7) Limpieza del usuario de demo
        servicios.autenticacion().logout();
        ConexionMongo.getBaseDatos().getCollection("usuarios")
                .deleteOne(new org.bson.Document("nombreUsuario", nombreUsuario));
        System.out.println("\n=== DEMO COMPLETADA CORRECTAMENTE ===");

        ConexionMongo.cerrar();
    }
}
