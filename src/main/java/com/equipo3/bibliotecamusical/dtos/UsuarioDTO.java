package com.equipo3.bibliotecamusical.dtos;

import java.time.LocalDate;
import java.util.List;

/** Vista segura de un usuario hacia la presentacion: NUNCA incluye la contrasena. */
public record UsuarioDTO(
        String id,
        String nombreUsuario,
        String correo,
        String imagenPerfil,
        LocalDate fechaRegistro,
        List<String> generosNoDeseados) {
}
