package com.equipo3.bibliotecamusical.negocio.mapeadores;

import com.equipo3.bibliotecamusical.dtos.UsuarioDTO;
import com.equipo3.bibliotecamusical.entidades.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversion de {@link Usuario} (dominio) a {@link UsuarioDTO} (presentacion).
 * No existe conversion inversa: el registro construye la entidad con la
 * contrasena ya hasheada en {@code AutenticacionService}.
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioDTO aDTO(Usuario u) {
        if (u == null) {
            return null;
        }
        List<String> generos = u.getGenerosNoDeseados() == null
                ? new ArrayList<>()
                : new ArrayList<>(u.getGenerosNoDeseados());
        return new UsuarioDTO(
                Ids.aHex(u.getId()), u.getNombreUsuario(), u.getCorreo(),
                u.getImagenPerfil(), u.getFechaRegistro(), generos);
    }
}
