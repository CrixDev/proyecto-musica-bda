package com.equipo3.bibliotecamusical.negocio.servicios;

import com.equipo3.bibliotecamusical.daos.IUsuarioDAO;
import com.equipo3.bibliotecamusical.dtos.ActualizarPerfilDTO;
import com.equipo3.bibliotecamusical.dtos.CambiarContrasenaDTO;
import com.equipo3.bibliotecamusical.dtos.UsuarioDTO;
import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.negocio.excepciones.AutenticacionException;
import com.equipo3.bibliotecamusical.negocio.excepciones.DuplicadoException;
import com.equipo3.bibliotecamusical.negocio.excepciones.NoEncontradoException;
import com.equipo3.bibliotecamusical.negocio.mapeadores.Ids;
import com.equipo3.bibliotecamusical.negocio.mapeadores.UsuarioMapper;
import com.equipo3.bibliotecamusical.negocio.seguridad.Passwords;
import com.equipo3.bibliotecamusical.negocio.validadores.ValidadorUsuario;
import com.equipo3.bibliotecamusical.persistencia.LlaveDuplicadaException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/** Consulta y edicion del perfil del usuario (sin favoritos/restricciones, que son fases posteriores). */
public class UsuarioService {

    private final IUsuarioDAO usuarioDAO;

    public UsuarioService(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public UsuarioDTO obtenerPerfil(String idHex) {
        return UsuarioMapper.aDTO(cargar(idHex));
    }

    public UsuarioDTO actualizarPerfil(String idHex, ActualizarPerfilDTO dto) {
        ValidadorUsuario.validarPerfil(dto);
        Usuario u = cargar(idHex);

        String nuevoUsuario = dto.nombreUsuario().trim();
        String nuevoCorreo = dto.correo().trim();
        if (!nuevoUsuario.equalsIgnoreCase(u.getNombreUsuario())
                && usuarioDAO.existeNombreUsuario(nuevoUsuario)) {
            throw new DuplicadoException("El nombre de usuario ya esta en uso");
        }
        if (!nuevoCorreo.equalsIgnoreCase(u.getCorreo())
                && usuarioDAO.existeCorreo(nuevoCorreo)) {
            throw new DuplicadoException("El correo ya esta registrado");
        }
        u.setNombreUsuario(nuevoUsuario);
        u.setCorreo(nuevoCorreo);
        u.setImagenPerfil(dto.imagenPerfil());
        try {
            usuarioDAO.actualizar(u);
        } catch (LlaveDuplicadaException e) {
            throw new DuplicadoException("El usuario o correo ya existe");
        }
        return UsuarioMapper.aDTO(u);
    }

    /** Persiste la lista de géneros no deseados del usuario. */
    public UsuarioDTO actualizarGenerosNoDeseados(String idHex, List<String> generos) {
        Usuario u = cargar(idHex);
        List<String> limpios = generos == null ? new ArrayList<>() : new ArrayList<>(generos);
        usuarioDAO.actualizarGenerosNoDeseados(u.getId(), limpios);
        u.setGenerosNoDeseados(limpios);
        return UsuarioMapper.aDTO(u);
    }

    public void cambiarContrasena(String idHex, CambiarContrasenaDTO dto) {
        ValidadorUsuario.validarCambioContrasena(dto);
        Usuario u = cargar(idHex);
        if (!Passwords.verificar(dto.contrasenaActual(), u.getContrasena())) {
            throw new AutenticacionException("La contrasena actual es incorrecta");
        }
        usuarioDAO.actualizarContrasena(u.getId(), Passwords.hash(dto.nuevaContrasena()));
    }

    private Usuario cargar(String idHex) {
        ObjectId id = Ids.aObjectId(idHex);
        return usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));
    }
}
