package com.equipo3.bibliotecamusical.dtos;

/** Credenciales de inicio de sesion. Se puede entrar con nombre de usuario o correo. */
public record CredencialesDTO(String usuarioOCorreo, String contrasena) {
}
