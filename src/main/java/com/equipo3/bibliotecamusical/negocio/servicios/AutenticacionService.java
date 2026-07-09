package com.equipo3.bibliotecamusical.negocio.servicios;

import com.equipo3.bibliotecamusical.daos.IUsuarioDAO;
import com.equipo3.bibliotecamusical.dtos.CredencialesDTO;
import com.equipo3.bibliotecamusical.dtos.RegistroUsuarioDTO;
import com.equipo3.bibliotecamusical.dtos.UsuarioDTO;
import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.negocio.excepciones.AutenticacionException;
import com.equipo3.bibliotecamusical.negocio.excepciones.DuplicadoException;
import com.equipo3.bibliotecamusical.negocio.mapeadores.UsuarioMapper;
import com.equipo3.bibliotecamusical.negocio.seguridad.Passwords;
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import com.equipo3.bibliotecamusical.negocio.validadores.ValidadorUsuario;
import com.equipo3.bibliotecamusical.negocio.validadores.Validaciones;
import com.equipo3.bibliotecamusical.persistencia.LlaveDuplicadaException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/** Registro, inicio y cierre de sesion. Nunca almacena contrasenas en texto plano. */
public class AutenticacionService {

    private final IUsuarioDAO usuarioDAO;
    private final SesionActual sesion;

    public AutenticacionService(IUsuarioDAO usuarioDAO, SesionActual sesion) {
        this.usuarioDAO = usuarioDAO;
        this.sesion = sesion;
    }

    public UsuarioDTO registrar(RegistroUsuarioDTO dto) {
        ValidadorUsuario.validarRegistro(dto);
        if (usuarioDAO.existeNombreUsuario(dto.nombreUsuario())) {
            throw new DuplicadoException("El nombre de usuario ya esta en uso");
        }
        if (usuarioDAO.existeCorreo(dto.correo())) {
            throw new DuplicadoException("El correo ya esta registrado");
        }
        Usuario u = new Usuario();
        u.setNombreUsuario(dto.nombreUsuario().trim());
        u.setCorreo(dto.correo().trim());
        u.setContrasena(Passwords.hash(dto.contrasena()));
        u.setImagenPerfil(dto.imagenPerfil());
        u.setFavoritos(new ArrayList<>());
        u.setGenerosNoDeseados(new ArrayList<>());
        u.setFechaRegistro(LocalDate.now());
        try {
            usuarioDAO.crear(u);
        } catch (LlaveDuplicadaException e) {
            throw new DuplicadoException("El usuario o correo ya existe");
        }
        return UsuarioMapper.aDTO(u);
    }

    public UsuarioDTO iniciarSesion(CredencialesDTO dto) {
        if (dto == null || Validaciones.vacio(dto.usuarioOCorreo()) || Validaciones.vacio(dto.contrasena())) {
            throw new AutenticacionException("Ingrese usuario/correo y contrasena");
        }
        Optional<Usuario> encontrado =
                usuarioDAO.buscarPorNombreUsuarioOCorreo(dto.usuarioOCorreo().trim());
        Usuario usuario = encontrado.orElseThrow(
                () -> new AutenticacionException("Usuario o contrasena incorrectos"));
        if (!Passwords.verificar(dto.contrasena(), usuario.getContrasena())) {
            throw new AutenticacionException("Usuario o contrasena incorrectos");
        }
        sesion.iniciar(usuario);
        return UsuarioMapper.aDTO(usuario);
    }

    public void cerrarSesion() {
        sesion.cerrar();
    }
}
