package com.equipo3.bibliotecamusical.daos;

import com.equipo3.bibliotecamusical.entidades.Usuario;
import java.util.Optional;
import org.bson.types.ObjectId;

/** Repositorio de usuarios (coleccion {@code usuarios}). */
public interface IUsuarioDAO {

    Usuario crear(Usuario usuario);

    Optional<Usuario> buscarPorId(ObjectId id);

    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    /** Para el login: busca por nombre de usuario O correo. */
    Optional<Usuario> buscarPorNombreUsuarioOCorreo(String valor);

    boolean existeNombreUsuario(String nombreUsuario);

    boolean existeCorreo(String correo);

    Usuario actualizar(Usuario usuario);

    void actualizarContrasena(ObjectId id, String hash);

    boolean eliminar(ObjectId id);
}
