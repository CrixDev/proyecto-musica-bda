package com.equipo3.bibliotecamusical.presentacion.vistas;

import com.equipo3.bibliotecamusical.dtos.FavoritoDTO;
import com.equipo3.bibliotecamusical.entidades.TipoFavorito;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.negocio.excepciones.NegocioException;
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonFavorito;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import com.equipo3.bibliotecamusical.presentacion.estilo.Formato;
import com.equipo3.bibliotecamusical.presentacion.estilo.Imagenes;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Pantalla de Favoritos: lista los favoritos <b>reales</b> del usuario en sesión
 * (artistas, álbumes y canciones), leídos desde MongoDB. Cada elemento es
 * clicable para ir a su detalle y trae un corazón para quitarlo (se persiste al
 * instante).
 */
public class PantallaFavoritos extends JPanel {

    private static final int LADO = 52;

    private final Servicios servicios;
    private final NavegacionVistas navegacion;
    private final JPanel contenido = new JPanel();

    public PantallaFavoritos(Servicios servicios, NavegacionVistas navegacion) {
        this.servicios = servicios;
        this.navegacion = navegacion != null ? navegacion : NavegacionVistas.NULA;
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);

        JLabel titulo = new JLabel("Favoritos");
        titulo.setFont(Estilos.TITULO_GRANDE);
        titulo.setForeground(Estilos.TEXTO_PRIMARIO);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 32, 4, 32));
        add(titulo, BorderLayout.NORTH);

        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(8, 32, 32, 32));

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Estilos.FONDO);
        add(scroll, BorderLayout.CENTER);

        recargar();
    }

    /** Vuelve a leer los favoritos del usuario y repinta la lista. */
    public void recargar() {
        contenido.removeAll();
        List<FavoritoDTO> favoritos = cargarFavoritos();

        if (favoritos.isEmpty()) {
            JLabel vacio = new JLabel("Aún no tienes favoritos. Marca el corazón en un artista, "
                    + "álbum o canción para agregarlo.");
            vacio.setFont(Estilos.TEXTO_NORMAL);
            vacio.setForeground(Estilos.TEXTO_SECUNDARIO);
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            vacio.setBorder(BorderFactory.createEmptyBorder(40, 4, 0, 0));
            contenido.add(vacio);
        } else {
            agregarSeccion("Artistas y álbumes", favoritos.stream()
                    .filter(f -> f.tipo() != TipoFavorito.CANCION).collect(Collectors.toList()));
            agregarSeccion("Canciones", favoritos.stream()
                    .filter(f -> f.tipo() == TipoFavorito.CANCION).collect(Collectors.toList()));
        }
        contenido.revalidate();
        contenido.repaint();
    }

    private void agregarSeccion(String titulo, List<FavoritoDTO> items) {
        if (items.isEmpty()) {
            return;
        }
        JLabel lbl = new JLabel(titulo + "  (" + items.size() + ")");
        lbl.setFont(Estilos.TITULO_MEDIANO);
        lbl.setForeground(Estilos.TEXTO_PRIMARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(16, 2, 10, 0));
        contenido.add(lbl);
        for (FavoritoDTO f : items) {
            contenido.add(fila(f));
            contenido.add(Box.createVerticalStrut(8));
        }
    }

    private JPanel fila(FavoritoDTO f) {
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setBackground(Estilos.SUPERFICIE);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        var img = Imagenes.cargar(f.imagen());
        JLabel icono = new JLabel(f.tipo() == TipoFavorito.ARTISTA
                ? new ImageIcon(Imagenes.circular(img, f.titulo(), LADO))
                : new ImageIcon(Imagenes.redondeada(img, f.titulo(), LADO, LADO, 10)));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel(f.titulo());
        titulo.setFont(Estilos.TEXTO_NEGRITA);
        titulo.setForeground(Estilos.TEXTO_PRIMARIO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        String sub = etiquetaTipo(f.tipo()) + " · " + (f.subtitulo() == null ? "" : f.subtitulo())
                + (f.tipo() == TipoFavorito.CANCION ? " · " + Formato.duracion(f.duracionSegundos()) : "");
        JLabel subtitulo = new JLabel(sub);
        subtitulo.setFont(Estilos.TEXTO_PEQUENO);
        subtitulo.setForeground(Estilos.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(subtitulo);

        JPanel izquierda = new JPanel(new BorderLayout(12, 0));
        izquierda.setOpaque(false);
        izquierda.setCursor(new Cursor(Cursor.HAND_CURSOR));
        izquierda.add(icono, BorderLayout.WEST);
        izquierda.add(textos, BorderLayout.CENTER);
        izquierda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navegar(f);
            }
        });
        fila.add(izquierda, BorderLayout.CENTER);

        BotonFavorito quitar = new BotonFavorito(true);
        quitar.setToolTipText("Quitar de favoritos");
        quitar.alCambiar(nuevo -> quitarFavorito(f));
        JPanel este = new JPanel(new BorderLayout());
        este.setOpaque(false);
        este.add(quitar, BorderLayout.CENTER);
        fila.add(este, BorderLayout.EAST);
        return fila;
    }

    private void navegar(FavoritoDTO f) {
        switch (f.tipo()) {
            case ARTISTA -> navegacion.irADetalleArtista(f.refId());
            case ALBUM -> navegacion.irADetalleAlbum(f.refId());
            case CANCION -> navegacion.irADetalleAlbum(f.albumId());
        }
    }

    private void quitarFavorito(FavoritoDTO f) {
        if (!SesionActual.hayUsuario()) {
            return;
        }
        try {
            servicios.favoritos().quitar(SesionActual.getUsuarioId().toHexString(), f.tipo(), f.refId());
        } catch (NegocioException e) {
            // Se ignora; se refresca de todos modos.
        }
        recargar();
    }

    private List<FavoritoDTO> cargarFavoritos() {
        if (servicios == null || !SesionActual.hayUsuario()) {
            return List.of();
        }
        try {
            return servicios.favoritos().listar(SesionActual.getUsuarioId().toHexString());
        } catch (NegocioException e) {
            return List.of();
        }
    }

    private static String etiquetaTipo(TipoFavorito tipo) {
        return switch (tipo) {
            case ARTISTA -> "Artista";
            case ALBUM -> "Álbum";
            case CANCION -> "Canción";
        };
    }
}
