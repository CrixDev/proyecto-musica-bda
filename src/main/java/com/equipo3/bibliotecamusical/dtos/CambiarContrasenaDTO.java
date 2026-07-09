package com.equipo3.bibliotecamusical.dtos;

/** Datos para el cambio de contrasena del usuario en sesion. */
public record CambiarContrasenaDTO(
        String contrasenaActual,
        String nuevaContrasena,
        String confirmarNueva) {
}
