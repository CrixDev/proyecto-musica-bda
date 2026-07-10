/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.vistas;

/**
 * Callbacks de navegacion que las pantallas de detalle (artista, album, ...)
 * necesitan pero no deben resolver por si mismas, porque eso es responsabilidad
 * del shell principal de la aplicacion (menu lateral + {@code CardLayout} o
 * similar, que arma otro companero del equipo).
 *
 * <p>
 * Cada pantalla recibe una implementacion de esta interfaz por constructor.
 * Asi, {@code VistaArtistaPanel} no conoce a {@code VistaAlbumPanel} ni
 * viceversa: solo piden "navega a este id" y el shell decide como.
 * 
 * @author Dylan
 */
public interface NavegacionVistas {

    /**
     * El usuario elige un album desde la lista de discos de un artista.
     */
    void irADetalleAlbum(String albumId);

    /**
     * El usuario elige un artista (p.ej. desde la ficha de una cancion o
     * busqueda).
     */
    void irADetalleArtista(String artistaId);

    /**
     * Boton "atras" de la pantalla.
     */
    void volver();

    /**
     * Implementacion vacia util para pruebas o para pantallas usadas de forma
     * aislada.
     */
    NavegacionVistas NULA = new NavegacionVistas() {
        @Override
        public void irADetalleAlbum(String albumId) {
        }

        @Override
        public void irADetalleArtista(String artistaId) {
        }

        @Override
        public void volver() {
        }
    };
}
