package com.equipo3.bibliotecamusical.negocio;

import com.equipo3.bibliotecamusical.daos.AlbumDAOImpl;
import com.equipo3.bibliotecamusical.daos.ArtistaDAOImpl;
import com.equipo3.bibliotecamusical.daos.IAlbumDAO;
import com.equipo3.bibliotecamusical.daos.IArtistaDAO;
import com.equipo3.bibliotecamusical.daos.IUsuarioDAO;
import com.equipo3.bibliotecamusical.daos.UsuarioDAOImpl;
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import com.equipo3.bibliotecamusical.negocio.servicios.AlbumService;
import com.equipo3.bibliotecamusical.negocio.servicios.ArtistaService;
import com.equipo3.bibliotecamusical.negocio.servicios.AutenticacionService;
import com.equipo3.bibliotecamusical.negocio.servicios.UsuarioService;
import com.mongodb.client.MongoDatabase;

/**
 * Raiz de composicion: construye DAOs y servicios a partir de una
 * {@link MongoDatabase}. La presentacion (o el runner) usa esta clase para
 * obtener los servicios ya cableados, sin conocer las implementaciones DAO.
 */
public class Servicios {

    private final SesionActual sesion;
    private final AutenticacionService autenticacion;
    private final UsuarioService usuarios;
    private final ArtistaService artistas;
    private final AlbumService albumes;

    public Servicios(MongoDatabase db) {
        IUsuarioDAO usuarioDAO = new UsuarioDAOImpl(db);
        IArtistaDAO artistaDAO = new ArtistaDAOImpl(db);
        IAlbumDAO albumDAO = new AlbumDAOImpl(db);

        this.sesion = new SesionActual();
        this.autenticacion = new AutenticacionService(usuarioDAO, sesion);
        this.usuarios = new UsuarioService(usuarioDAO);
        this.artistas = new ArtistaService(artistaDAO, albumDAO);
        this.albumes = new AlbumService(albumDAO, artistaDAO);
    }

    public SesionActual sesion() {
        return sesion;
    }

    public AutenticacionService autenticacion() {
        return autenticacion;
    }

    public UsuarioService usuarios() {
        return usuarios;
    }

    public ArtistaService artistas() {
        return artistas;
    }

    public AlbumService albumes() {
        return albumes;
    }
}
