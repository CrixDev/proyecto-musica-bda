package com.equipo3.bibliotecamusical.negocio.validadores;

import com.equipo3.bibliotecamusical.dtos.ActualizarPerfilDTO;
import com.equipo3.bibliotecamusical.dtos.CambiarContrasenaDTO;
import com.equipo3.bibliotecamusical.dtos.RegistroUsuarioDTO;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import java.util.ArrayList;
import java.util.List;

/** Reglas de validacion para usuarios (registro, perfil y contrasena). */
public final class ValidadorUsuario {

    private static final int MIN_USUARIO = 3;
    private static final int MIN_CONTRASENA = 6;

    private ValidadorUsuario() {
    }

    public static void validarRegistro(RegistroUsuarioDTO dto) {
        List<String> errores = new ArrayList<>();
        if (dto == null) {
            throw new ValidacionException("Datos de registro nulos");
        }
        if (Validaciones.vacio(dto.nombreUsuario()) || dto.nombreUsuario().trim().length() < MIN_USUARIO) {
            errores.add("El nombre de usuario debe tener al menos " + MIN_USUARIO + " caracteres");
        }
        if (dto.nombreUsuario() != null && dto.nombreUsuario().contains(" ")) {
            errores.add("El nombre de usuario no debe contener espacios");
        }
        if (!Validaciones.correoValido(dto.correo())) {
            errores.add("El correo no tiene un formato valido");
        }
        validarContrasena(dto.contrasena(), dto.confirmarContrasena(), errores);
        lanzarSiHay(errores);
    }

    public static void validarPerfil(ActualizarPerfilDTO dto) {
        List<String> errores = new ArrayList<>();
        if (dto == null) {
            throw new ValidacionException("Datos de perfil nulos");
        }
        if (Validaciones.vacio(dto.nombreUsuario()) || dto.nombreUsuario().trim().length() < MIN_USUARIO) {
            errores.add("El nombre de usuario debe tener al menos " + MIN_USUARIO + " caracteres");
        }
        if (!Validaciones.correoValido(dto.correo())) {
            errores.add("El correo no tiene un formato valido");
        }
        lanzarSiHay(errores);
    }

    public static void validarCambioContrasena(CambiarContrasenaDTO dto) {
        List<String> errores = new ArrayList<>();
        if (dto == null) {
            throw new ValidacionException("Datos de cambio de contrasena nulos");
        }
        if (Validaciones.vacio(dto.contrasenaActual())) {
            errores.add("Debe ingresar su contrasena actual");
        }
        validarContrasena(dto.nuevaContrasena(), dto.confirmarNueva(), errores);
        lanzarSiHay(errores);
    }

    private static void validarContrasena(String contrasena, String confirmacion, List<String> errores) {
        if (Validaciones.vacio(contrasena) || contrasena.length() < MIN_CONTRASENA) {
            errores.add("La contrasena debe tener al menos " + MIN_CONTRASENA + " caracteres");
        } else if (!contrasena.equals(confirmacion)) {
            errores.add("La confirmacion de contrasena no coincide");
        }
    }

    private static void lanzarSiHay(List<String> errores) {
        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }
    }
}
