package com.equipo3.bibliotecamusical.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 * Usuario (documento de la coleccion {@code usuarios}). La contrasena se almacena
 * siempre como hash bcrypt, nunca en texto plano. Embebe sus {@link Favorito}
 * (como referencias) y sus generos restringidos.
 */
public class Usuario {

    private ObjectId id;
    private String nombreUsuario;
    private String correo;
    private String contrasena;
    private String imagenPerfil;
    private List<Favorito> favoritos = new ArrayList<>();
    private List<String> generosNoDeseados = new ArrayList<>();
    private LocalDate fechaRegistro;

    public Usuario() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public List<Favorito> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<Favorito> favoritos) {
        this.favoritos = favoritos;
    }

    public List<String> getGenerosNoDeseados() {
        return generosNoDeseados;
    }

    public void setGenerosNoDeseados(List<String> generosNoDeseados) {
        this.generosNoDeseados = generosNoDeseados;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
