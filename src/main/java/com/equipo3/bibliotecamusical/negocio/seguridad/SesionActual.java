package com.equipo3.bibliotecamusical.negocio.seguridad;

import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.negocio.excepciones.NegocioException;
import org.bson.types.ObjectId;

/**
 * Mantiene en memoria el usuario autenticado durante la ejecucion (app de
 * escritorio mono-usuario por sesion). Se limpia al cerrar sesion.
 */
public class SesionActual {

    private Usuario usuario;

    public void iniciar(Usuario usuario) {
        this.usuario = usuario;
    }

    public void cerrar() {
        this.usuario = null;
    }

    public boolean hayUsuario() {
        return usuario != null;
    }

    public Usuario getUsuario() {
        if (usuario == null) {
            throw new NegocioException("No hay una sesion activa");
        }
        return usuario;
    }

    public ObjectId getUsuarioId() {
        return getUsuario().getId();
    }
}
