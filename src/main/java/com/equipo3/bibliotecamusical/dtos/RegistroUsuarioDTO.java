package com.equipo3.bibliotecamusical.dtos;

/** Datos capturados en el formulario de registro. La contrasena va en texto plano
 *  solo en este DTO de entrada; el negocio la hashea antes de persistir. */
public record RegistroUsuarioDTO(
        String nombreUsuario,
        String correo,
        String contrasena,
        String confirmarContrasena,
        String imagenPerfil) {
}
