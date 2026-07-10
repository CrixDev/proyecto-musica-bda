package com.equipo3.bibliotecamusical.negocio.seguridad;

import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.negocio.excepciones.NegocioException;
import org.bson.types.ObjectId;

/**
 * Mantiene en memoria el usuario autenticado durante la ejecucion (app de
 * escritorio mono-usuario por sesion). Se limpia al cerrar sesion.
 */
public class SesionActual {

    // 1. El atributo debe ser estático para que persista durante toda la ejecución de la app
    private static Usuario usuario;

    // Constructor privado para evitar que alguien intente hacer un: new SesionActual()
    private SesionActual() {}

    // 2. Volver los métodos estáticos para poder llamarlos como SesionActual.iniciar(...)
    public static void iniciar(Usuario usuarioLogueado) {
        usuario = usuarioLogueado;
    }

    public static void cerrar() {
        usuario = null;
    }

    public static boolean hayUsuario() {
        return usuario != null;
    }

    public static Usuario getUsuario() {
        if (usuario == null) {
            // Lanza tu excepción personalizada si intentan acceder al usuario sin haber hecho login
            throw new NegocioException("No hay una sesión activa en el sistema.");
        }
        return usuario;
    }

    public static ObjectId getUsuarioId() {
        return getUsuario().getId();
    }
}