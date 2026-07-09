package com.equipo3.bibliotecamusical.dtos;

/** Datos editables del perfil del usuario (no incluye contrasena). */
public record ActualizarPerfilDTO(String nombreUsuario, String correo, String imagenPerfil) {
}
