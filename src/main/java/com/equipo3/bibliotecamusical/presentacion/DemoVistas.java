/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.CancionDTO;
import com.equipo3.bibliotecamusical.dtos.IntegranteDTO;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.persistencia.ConexionMongo;
import com.equipo3.bibliotecamusical.persistencia.InicializadorBd;
import com.equipo3.bibliotecamusical.presentacion.vistas.NavegacionVistas;
import com.equipo3.bibliotecamusical.presentacion.vistas.VistaAlbumPanel;
import com.equipo3.bibliotecamusical.presentacion.vistas.VistaArtistaPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Ventana suelta para previsualizar {@link VistaArtistaPanel} y
 * {@link VistaAlbumPanel} SIN esperar a que el resto del equipo termine el
 * shell principal (sidebar, login, buscador). No es el punto de entrada final
 * de la app: solo sirve para revisar visualmente estas dos pantallas y como
 * ejemplo de como conectarlas con {@link NavegacionVistas}.
 *
 * <p>
 * Si hay una instancia de MongoDB corriendo en localhost:27017 con datos
 * cargados, se muestra el primer artista encontrado en la base. Si no, se usan
 * datos de muestra (identicos en espiritu al storyboard: una banda con dos
 * discos) para que la pantalla se pueda revisar sin depender de Mongo.
 *
 * <p>
 * Ejecutar con: {@code mvn -q compile exec:java
 * -Dexec.mainClass=com.equipo3.bibliotecamusical.presentacion.DemoVistas}
 * 
 * @author Dylan
 */
public final class DemoVistas {

    private DemoVistas() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DemoVistas::construirVentana);
    }

    private static void construirVentana() {
        JFrame frame = new JFrame("Biblioteca Musical - Demo vistas (artista / album)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 700));

        CardLayout cardLayout = new CardLayout();
        JPanel raiz = new JPanel(cardLayout);

        Servicios servicios = crearServicios();

        NavegacionVistas navegacion = new NavegacionVistas() {
            @Override
            public void irADetalleAlbum(String albumId) {
                mostrarAlbum(raiz, cardLayout, servicios, this, albumId);
            }

            @Override
            public void irADetalleArtista(String artistaId) {
                mostrarArtista(raiz, cardLayout, servicios, this, artistaId);
            }

            @Override
            public void volver() {
                cardLayout.show(raiz, "artista");
            }
        };

        VistaArtistaPanel vistaArtista = new VistaArtistaPanel(servicios, navegacion);
        VistaAlbumPanel vistaAlbum = new VistaAlbumPanel(servicios, navegacion);
        raiz.add(vistaArtista, "artista");
        raiz.add(vistaAlbum, "album");

        DatosMuestra muestra = DatosMuestra.arcticMonkeys();
        if (servicios != null) {
            // Hay Mongo disponible: se intenta mostrar el primer artista real;
            // si la coleccion esta vacia, se cae de vuelta a los datos de muestra.
            List<ArtistaDTO> existentes = servicios.artistas().listar();
            if (!existentes.isEmpty()) {
                vistaArtista.cargar(existentes.get(0).id());
            } else {
                vistaArtista.mostrar(muestra.artista, muestra.albumes);
            }
        } else {
            vistaArtista.mostrar(muestra.artista, muestra.albumes);
        }
        vistaAlbum.mostrar(muestra.albumes.get(0), muestra.artista);

        frame.setContentPane(raiz);
        cardLayout.show(raiz, "artista");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void mostrarAlbum(JPanel raiz, CardLayout cardLayout, Servicios servicios,
            NavegacionVistas navegacion, String albumId) {
        VistaAlbumPanel vista = new VistaAlbumPanel(servicios, navegacion);
        vista.cargar(albumId);
        raiz.add(vista, "album");
        cardLayout.show(raiz, "album");
    }

    private static void mostrarArtista(JPanel raiz, CardLayout cardLayout, Servicios servicios,
            NavegacionVistas navegacion, String artistaId) {
        VistaArtistaPanel vista = new VistaArtistaPanel(servicios, navegacion);
        vista.cargar(artistaId);
        raiz.add(vista, "artista");
        cardLayout.show(raiz, "artista");
    }

    /**
     * Intenta conectar a Mongo; si no esta disponible, regresa null y se usan
     * datos de muestra.
     */
    private static Servicios crearServicios() {
        if (!ConexionMongo.disponible()) {
            return null;
        }
        InicializadorBd.inicializar(ConexionMongo.getBaseDatos());
        return new Servicios(ConexionMongo.getBaseDatos());
    }

    /**
     * Datos de muestra usados cuando no hay Mongo corriendo, inspirados en el
     * storyboard de Figma.
     */
    private static final class DatosMuestra {

        final ArtistaDTO artista;
        final List<AlbumDTO> albumes;

        private DatosMuestra(ArtistaDTO artista, List<AlbumDTO> albumes) {
            this.artista = artista;
            this.albumes = albumes;
        }

        static DatosMuestra arcticMonkeys() {
            ArtistaDTO artista = new ArtistaDTO(
                    "demo-artista-1", TipoArtista.BANDA, "Arctic Monkeys", null, "Indie",
                    LocalDate.of(2002, 1, 1),
                    List.of(
                            new IntegranteDTO("Alex Turner", "Vocalista", LocalDate.of(2006, 1, 1), null, true),
                            new IntegranteDTO("Matt Helders", "Bateria", LocalDate.of(2008, 1, 1), null, true),
                            new IntegranteDTO("Benito Martinez", "Bajo", LocalDate.of(2019, 1, 1), null, true)));

            AlbumDTO am = new AlbumDTO("demo-album-1", artista.id(), "AM",
                    LocalDate.of(2017, 6, 9), "Rock", null,
                    List.of(
                            new CancionDTO("c1", "Do I Wanna Know", 1, 222, "Rock"),
                            new CancionDTO("c2", "R U Mine?", 2, 200, "Rock"),
                            new CancionDTO("c3", "Arabella", 3, 188, "Rock"),
                            new CancionDTO("c4", "Fireside", 4, 301, "Rock"),
                            new CancionDTO("c5", "I Wanna Be Yours", 5, 193, "Rock")));

            AlbumDTO theView = new AlbumDTO("demo-album-2", artista.id(), "The View for Afternoon",
                    LocalDate.of(2024, 3, 1), "Indie", null,
                    List.of(
                            new CancionDTO("c6", "Tema 1", 1, 210, "Indie"),
                            new CancionDTO("c7", "Tema 2", 2, 195, "Indie"),
                            new CancionDTO("c8", "Tema 3", 3, 240, "Indie")));

            return new DatosMuestra(artista, List.of(am, theView));
        }
    }
}
