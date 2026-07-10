/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.vistas;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.CancionDTO;
import com.equipo3.bibliotecamusical.entidades.TipoFavorito;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.negocio.excepciones.NegocioException;
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonFavorito;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonPildora;
import com.equipo3.bibliotecamusical.presentacion.componentes.EtiquetaBadge;
import com.equipo3.bibliotecamusical.presentacion.componentes.PanelDegradado;
import com.equipo3.bibliotecamusical.presentacion.componentes.ReproductorSimulado;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import com.equipo3.bibliotecamusical.presentacion.estilo.Formato;
import com.equipo3.bibliotecamusical.presentacion.estilo.Imagenes;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Pantalla individual de album: portada, boton "Reproducir", favorito y la
 * lista de canciones con su duracion, calcada del storyboard de Figma.
 *
 * <p>
 * Uso tipico desde el shell principal:
 * <pre>{@code
 * VistaAlbumPanel vista = new VistaAlbumPanel(servicios, navegacion);
 * vista.cargar(albumId);
 * panelContenedor.add(vista);
 * }</pre>
 * 
 * @author Dylan
 */
public class VistaAlbumPanel extends JPanel {

    private static final int LADO_PORTADA = 168;

    private final Servicios servicios;
    private final NavegacionVistas navegacion;

    private final JPanel contenedorPrincipal = new JPanel(new BorderLayout());
    private final ReproductorSimulado reproductor = new ReproductorSimulado();
    private Runnable alReproducir = () -> {
    };

    public VistaAlbumPanel(Servicios servicios, NavegacionVistas navegacion) {
        this.servicios = servicios;
        this.navegacion = navegacion != null ? navegacion : NavegacionVistas.NULA;
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);
        add(construirBarraSuperior(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(contenedorPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Estilos.FONDO);
        add(scroll, BorderLayout.CENTER);

        // Barra inferior del reproductor simulado (oculta hasta que se reproduce).
        add(reproductor, BorderLayout.SOUTH);
    }

    /**
     * Hook opcional para cuando la fase de reproduccion este lista.
     */
    public void alReproducir(Runnable callback) {
        this.alReproducir = callback != null ? callback : () -> {
        };
    }

    /**
     * Carga (o recarga) la pantalla consultando al servicio de negocio por id.
     */
    public void cargar(String albumId) {
        contenedorPrincipal.removeAll();
        try {
            AlbumDTO album = servicios.albumes().obtener(albumId);
            ArtistaDTO artista = servicios.artistas().obtener(album.artistaId());
            mostrar(album, artista);
        } catch (NegocioException e) {
            contenedorPrincipal.add(mensajeError(e.getMessage()), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    /**
     * Pinta la pantalla con datos ya resueltos (sin volver a golpear la base de
     * datos). Util cuando quien navega ya tiene el {@link AlbumDTO} a la mano y
     * para pruebas/demo sin Mongo.
     */
    public void mostrar(AlbumDTO album, ArtistaDTO artista) {
        reproductor.detener(); // al cambiar de álbum se detiene lo que estuviera sonando
        contenedorPrincipal.removeAll();
        contenedorPrincipal.add(construirBanner(album, artista), BorderLayout.NORTH);
        contenedorPrincipal.add(construirListaCanciones(album, artista), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ------------------------------------------------------------- barra sup.
    private JPanel construirBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.FONDO);
        barra.setBorder(BorderFactory.createEmptyBorder(14, 18, 0, 18));

        JButton volver = new JButton("\u2190");
        volver.setFocusPainted(false);
        volver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        volver.setFont(Estilos.TITULO_MEDIANO);
        volver.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        volver.addActionListener(e -> navegacion.volver());
        barra.add(volver, BorderLayout.WEST);
        // Nota: el chip de usuario (avatar + nombre) de la esquina superior
        // derecha del storyboard es parte del shell/topbar compartido de la
        // app y lo agrega quien arme la ventana principal, no esta pantalla.
        return barra;
    }

    // ---------------------------------------------------------------- banner
    private JPanel construirBanner(AlbumDTO album, ArtistaDTO artista) {
        PanelDegradado banner = new PanelDegradado();
        banner.setLayout(new BorderLayout());
        banner.setBorder(BorderFactory.createEmptyBorder(24, 32, 28, 32));

        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.X_AXIS));

        var portadaOrigen = Imagenes.cargar(album.imagenPortada());
        JLabel portada = new JLabel(new ImageIcon(
                Imagenes.redondeada(portadaOrigen, album.nombre(), LADO_PORTADA, LADO_PORTADA, 12)));
        portada.setAlignmentY(Component.TOP_ALIGNMENT);
        fila.add(portada);
        fila.add(Box.createHorizontalStrut(24));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setAlignmentY(Component.TOP_ALIGNMENT);

        EtiquetaBadge badge = EtiquetaBadge.morada("\u00C1lbum \u00B7 " + album.genero());
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(badge);
        textos.add(Box.createVerticalStrut(10));

        JLabel nombre = new JLabel(album.nombre());
        nombre.setFont(Estilos.TITULO_GRANDE);
        nombre.setForeground(Estilos.TEXTO_SOBRE_BANNER);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(nombre);
        textos.add(Box.createVerticalStrut(6));

        String subtitulo = artista.nombre() + " \u00B7 " + Formato.anio(album.fechaLanzamiento())
                + " \u00B7 " + Formato.canciones(album.canciones().size())
                + " \u00B7 " + Formato.duracionTotal(album.canciones());
        JLabel subLabel = new JLabel(subtitulo);
        subLabel.setFont(Estilos.SUBTITULO);
        subLabel.setForeground(new java.awt.Color(255, 255, 255, 180));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        subLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navegacion.irADetalleArtista(artista.id());
            }
        });
        textos.add(subLabel);
        textos.add(Box.createVerticalStrut(18));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        acciones.setOpaque(false);
        acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        BotonPildora reproducir = new BotonPildora("\u25B6  Reproducir", true);
        reproducir.addActionListener(e -> {
            alReproducir.run();
            if (album.canciones() != null && !album.canciones().isEmpty()) {
                CancionDTO primera = album.canciones().get(0);
                reproductor.reproducir(primera.nombre(), artista.nombre(), primera.duracionSegundos());
            }
        });
        BotonFavorito favorito = new BotonFavorito(esFavorito(TipoFavorito.ALBUM, album.id()));
        favorito.alCambiar(nuevo -> alternarFavorito(
                TipoFavorito.ALBUM, album.id(), null, album.genero(), nuevo, favorito));
        acciones.add(reproducir);
        acciones.add(favorito);
        textos.add(acciones);

        fila.add(textos);
        banner.add(fila, BorderLayout.WEST);
        return banner;
    }

    // --------------------------------------------------------- lista de temas
    private JPanel construirListaCanciones(AlbumDTO album, ArtistaDTO artista) {
        JPanel seccion = new JPanel();
        seccion.setOpaque(false);
        seccion.setLayout(new BoxLayout(seccion, BoxLayout.Y_AXIS));
        seccion.setBorder(BorderFactory.createEmptyBorder(20, 32, 32, 32));

        seccion.add(encabezadoTabla());
        seccion.add(Box.createVerticalStrut(4));
        seccion.add(lineaSeparadora());

        List<CancionDTO> canciones = album.canciones();
        for (CancionDTO cancion : canciones) {
            seccion.add(filaCancion(cancion, artista.nombre(), album.id(), album.genero()));
            seccion.add(lineaSeparadora());
        }
        return seccion;
    }

    private JPanel encabezadoTabla() {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        izquierda.setOpaque(false);
        JLabel numero = etiquetaEncabezado("#");
        numero.setPreferredSize(new Dimension(18, 16));
        JLabel titulo = etiquetaEncabezado("T\u00CDTULO");
        izquierda.add(numero);
        izquierda.add(titulo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 0));
        derecha.setOpaque(false);
        derecha.add(etiquetaEncabezado("ARTISTA"));
        derecha.add(etiquetaEncabezado("\u23F1"));
        derecha.add(Box.createHorizontalStrut(34));

        fila.add(izquierda, BorderLayout.WEST);
        fila.add(derecha, BorderLayout.EAST);
        return fila;
    }

    private JLabel etiquetaEncabezado(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(Estilos.TEXTO_PEQUENO);
        label.setForeground(Estilos.TEXTO_SECUNDARIO);
        return label;
    }

    private JPanel filaCancion(CancionDTO cancion, String nombreArtista, String albumId, String generoAlbum) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        izquierda.setOpaque(false);
        JLabel numero = new JLabel(String.valueOf(cancion.numeroPista()));
        numero.setFont(Estilos.TEXTO_NORMAL);
        numero.setForeground(Estilos.TEXTO_SECUNDARIO);
        numero.setPreferredSize(new Dimension(18, 16));
        JLabel nombre = new JLabel(cancion.nombre());
        nombre.setFont(Estilos.TEXTO_NEGRITA);
        nombre.setForeground(Estilos.TEXTO_PRIMARIO);
        izquierda.add(numero);
        izquierda.add(nombre);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 0));
        derecha.setOpaque(false);
        JLabel artistaLabel = new JLabel(nombreArtista);
        artistaLabel.setFont(Estilos.TEXTO_NORMAL);
        artistaLabel.setForeground(Estilos.TEXTO_SECUNDARIO);
        JLabel duracion = new JLabel(Formato.duracion(cancion.duracionSegundos()));
        duracion.setFont(Estilos.TEXTO_NORMAL);
        duracion.setForeground(Estilos.TEXTO_SECUNDARIO);
        String generoCancion = cancion.genero() != null ? cancion.genero() : generoAlbum;
        JButton play = botonPlay();
        play.addActionListener(e -> reproductor.reproducir(
                cancion.nombre(), nombreArtista, cancion.duracionSegundos()));
        BotonFavorito favorito = new BotonFavorito(esFavorito(TipoFavorito.CANCION, cancion.id()));
        favorito.alCambiar(nuevo -> alternarFavorito(
                TipoFavorito.CANCION, cancion.id(), albumId, generoCancion, nuevo, favorito));
        derecha.add(artistaLabel);
        derecha.add(duracion);
        derecha.add(play);
        derecha.add(favorito);

        fila.add(izquierda, BorderLayout.WEST);
        fila.add(derecha, BorderLayout.EAST);
        return fila;
    }

    private JPanel lineaSeparadora() {
        JPanel linea = new JPanel();
        linea.setBackground(Estilos.BORDE);
        linea.setPreferredSize(new Dimension(10, 1));
        linea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linea.setAlignmentX(Component.LEFT_ALIGNMENT);
        return linea;
    }

    private JButton botonPlay() {
        JButton b = new JButton("▶");
        b.setFont(Estilos.TEXTO_NORMAL);
        b.setForeground(Estilos.ACENTO_TEAL_OSCURO);
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setToolTipText("Reproducir (simulado)");
        return b;
    }

    // ---------------------------------------------------------- favoritos

    private boolean esFavorito(TipoFavorito tipo, String refId) {
        if (servicios == null || !SesionActual.hayUsuario() || refId == null) {
            return false;
        }
        try {
            return servicios.favoritos().esFavorito(
                    SesionActual.getUsuarioId().toHexString(), tipo, refId);
        } catch (NegocioException e) {
            return false;
        }
    }

    private void alternarFavorito(TipoFavorito tipo, String refId, String albumId,
            String genero, boolean deseado, BotonFavorito boton) {
        if (servicios == null || !SesionActual.hayUsuario()) {
            return;
        }
        try {
            servicios.favoritos().alternar(SesionActual.getUsuarioId().toHexString(),
                    tipo, refId, albumId, genero, deseado);
        } catch (NegocioException e) {
            boton.setActivo(!deseado);
        }
    }

    private JPanel mensajeError(String mensaje) {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        JLabel label = new JLabel(mensaje != null ? mensaje : "No se pudo cargar el album.");
        label.setFont(Estilos.TEXTO_NORMAL);
        label.setForeground(Estilos.TEXTO_SECUNDARIO);
        panel.add(label);
        return panel;
    }
}
