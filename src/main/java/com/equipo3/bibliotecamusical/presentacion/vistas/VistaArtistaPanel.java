/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.vistas;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.IntegranteDTO;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.negocio.excepciones.NegocioException;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonFavorito;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonPildora;
import com.equipo3.bibliotecamusical.presentacion.componentes.EtiquetaBadge;
import com.equipo3.bibliotecamusical.presentacion.componentes.PanelDegradado;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Pantalla individual de artista (requerimiento 4: "consultar una lista de
 * artistas... al seleccionar una banda se desplegara la lista detallada de
 * integrantes, mostrando solo los activos por defecto").
 *
 * <p>
 * Reproduce la maqueta de Figma: banner con foto circular, insignia de
 * tipo/genero, nombre, boton "Reproducir" + favorito, grid de discos y (solo
 * para bandas) panel lateral de integrantes con el toggle de inactivos.
 *
 * <p>
 * Uso tipico desde el shell principal:
 * <pre>{@code
 * VistaArtistaPanel vista = new VistaArtistaPanel(servicios, navegacion);
 * vista.cargar(artistaId);
 * panelContenedor.add(vista);
 * }</pre>
 * 
 * @author Dylan
 */
public class VistaArtistaPanel extends JPanel {

    private static final int LADO_FOTO = 128;
    private static final int ANCHO_TARJETA_ALBUM = 170;

    private final Servicios servicios;
    private final NavegacionVistas navegacion;

    private final JPanel contenedorPrincipal = new JPanel(new BorderLayout());
    private Runnable alReproducir = () -> {
    };

    public VistaArtistaPanel(Servicios servicios, NavegacionVistas navegacion) {
        this.servicios = servicios;
        this.navegacion = navegacion != null ? navegacion : NavegacionVistas.NULA;
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);
        JScrollPane scroll = new JScrollPane(contenedorPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Estilos.FONDO);
        add(scroll, BorderLayout.CENTER);
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
    public void cargar(String artistaId) {
        contenedorPrincipal.removeAll();
        try {
            ArtistaDTO artista = servicios.artistas().obtener(artistaId);
            List<AlbumDTO> albumes = servicios.albumes().listarPorArtista(artistaId);
            mostrar(artista, albumes);
        } catch (NegocioException e) {
            contenedorPrincipal.add(mensajeError(e.getMessage()), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    /**
     * Pinta la pantalla con datos ya resueltos (sin volver a golpear la base de
     * datos). Util cuando quien navega ya tiene el {@link ArtistaDTO} a la mano
     * (p.ej. desde una tarjeta de lista) y para pruebas/demo sin Mongo.
     */
    public void mostrar(ArtistaDTO artista, List<AlbumDTO> albumes) {
        contenedorPrincipal.removeAll();
        contenedorPrincipal.add(construirBanner(artista, albumes), BorderLayout.NORTH);
        contenedorPrincipal.add(construirCuerpo(artista, albumes), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ---------------------------------------------------------------- banner
    private JPanel construirBanner(ArtistaDTO artista, List<AlbumDTO> albumes) {
        PanelDegradado banner = new PanelDegradado();
        banner.setLayout(new BorderLayout());
        banner.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.X_AXIS));

        var imagen = Imagenes.cargar(artista.imagen());
        JLabel foto = new JLabel(new ImageIcon(Imagenes.circular(imagen, artista.nombre(), LADO_FOTO)));
        foto.setAlignmentY(Component.TOP_ALIGNMENT);
        fila.add(foto);
        fila.add(Box.createHorizontalStrut(24));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setAlignmentY(Component.TOP_ALIGNMENT);

        String tipoTexto = artista.tipo() == TipoArtista.BANDA ? "Banda" : "Solista";
        EtiquetaBadge badge = EtiquetaBadge.verde(tipoTexto + " \u00B7 " + artista.genero());
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(badge);
        textos.add(Box.createVerticalStrut(10));

        JLabel nombre = new JLabel(artista.nombre());
        nombre.setFont(Estilos.TITULO_GRANDE);
        nombre.setForeground(Estilos.TEXTO_SOBRE_BANNER);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(nombre);
        textos.add(Box.createVerticalStrut(6));

        int totalCanciones = albumes.stream().mapToInt(a -> a.canciones().size()).sum();
        JLabel subtitulo = new JLabel(Formato.albumes(albumes.size()) + " \u00B7 " + Formato.canciones(totalCanciones));
        subtitulo.setFont(Estilos.SUBTITULO);
        subtitulo.setForeground(new java.awt.Color(255, 255, 255, 180));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(subtitulo);
        textos.add(Box.createVerticalStrut(18));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        acciones.setOpaque(false);
        acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        BotonPildora reproducir = new BotonPildora("\u25B6  Reproducir", true);
        reproducir.addActionListener(e -> alReproducir.run());
        BotonFavorito favorito = new BotonFavorito(false);
        acciones.add(reproducir);
        acciones.add(favorito);
        textos.add(acciones);

        fila.add(textos);
        banner.add(fila, BorderLayout.WEST);
        return banner;
    }

    // ----------------------------------------------------------------- cuerpo
    private JPanel construirCuerpo(ArtistaDTO artista, List<AlbumDTO> albumes) {
        JPanel cuerpo = new JPanel(new BorderLayout(28, 0));
        cuerpo.setBackground(Estilos.FONDO);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(24, 32, 32, 32));
        cuerpo.add(construirSeccionAlbumes(albumes), BorderLayout.CENTER);

        // Se muestra el panel de integrantes para cualquier artista que tenga
        // integrantes: una banda con sus miembros o un solista que se registra a
        // si mismo como integrante.
        if (artista.integrantes() != null && !artista.integrantes().isEmpty()) {
            JPanel integrantes = construirSeccionIntegrantes(artista.integrantes());
            integrantes.setPreferredSize(new Dimension(240, 0));
            cuerpo.add(integrantes, BorderLayout.EAST);
        }
        return cuerpo;
    }

    private JPanel construirSeccionAlbumes(List<AlbumDTO> albumes) {
        JPanel seccion = new JPanel();
        seccion.setOpaque(false);
        seccion.setLayout(new BoxLayout(seccion, BoxLayout.Y_AXIS));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titulo = new JLabel("\u00C1lbumes");
        titulo.setFont(Estilos.TITULO_MEDIANO);
        titulo.setForeground(Estilos.TEXTO_PRIMARIO);
        JLabel conteo = new JLabel(Formato.albumes(albumes.size()));
        conteo.setFont(Estilos.TEXTO_NORMAL);
        conteo.setForeground(Estilos.ACENTO_TEAL_OSCURO);
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(conteo, BorderLayout.EAST);
        seccion.add(encabezado);
        seccion.add(Box.createVerticalStrut(16));

        JPanel grid = new JPanel(new java.awt.FlowLayout(FlowLayout.LEFT, 20, 20));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (albumes.isEmpty()) {
            grid.add(new JLabel("Este artista todavia no tiene albumes registrados."));
        } else {
            for (AlbumDTO album : albumes) {
                grid.add(tarjetaAlbum(album));
            }
        }
        seccion.add(grid);
        return seccion;
    }

    private JPanel tarjetaAlbum(AlbumDTO album) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setOpaque(false);
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.setPreferredSize(new Dimension(ANCHO_TARJETA_ALBUM, ANCHO_TARJETA_ALBUM + 46));
        tarjeta.setMaximumSize(tarjeta.getPreferredSize());

        var portadaOrigen = Imagenes.cargar(album.imagenPortada());
        JLabel portada = new JLabel(new ImageIcon(
                Imagenes.redondeada(portadaOrigen, album.nombre(), ANCHO_TARJETA_ALBUM, ANCHO_TARJETA_ALBUM, 14)));
        portada.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(portada);
        tarjeta.add(Box.createVerticalStrut(8));

        JLabel nombre = new JLabel(album.nombre());
        nombre.setFont(Estilos.TEXTO_NEGRITA);
        nombre.setForeground(Estilos.TEXTO_PRIMARIO);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(nombre);

        JLabel detalle = new JLabel(Formato.anio(album.fechaLanzamiento()) + " \u00B7 "
                + Formato.canciones(album.canciones().size()));
        detalle.setFont(Estilos.TEXTO_PEQUENO);
        detalle.setForeground(Estilos.TEXTO_SECUNDARIO);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(detalle);

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navegacion.irADetalleAlbum(album.id());
            }
        });
        return tarjeta;
    }

    private JPanel construirSeccionIntegrantes(List<IntegranteDTO> integrantes) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilos.SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE, 1, true),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel titulo = new JLabel("Integrantes");
        titulo.setFont(Estilos.TEXTO_NEGRITA);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));

        JCheckBox mostrarInactivos = new JCheckBox("Mostrar tambien inactivos");
        mostrarInactivos.setOpaque(false);
        mostrarInactivos.setFont(Estilos.TEXTO_PEQUENO);
        mostrarInactivos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mostrarInactivos);
        panel.add(Box.createVerticalStrut(10));

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lista);

        Consumer<Boolean> repintar = incluirInactivos -> {
            lista.removeAll();
            List<IntegranteDTO> visibles = integrantes.stream()
                    .filter(i -> incluirInactivos || i.activo())
                    .collect(Collectors.toList());
            if (visibles.isEmpty()) {
                JLabel vacio = new JLabel("Sin integrantes para mostrar.");
                vacio.setFont(Estilos.TEXTO_PEQUENO);
                vacio.setForeground(Estilos.TEXTO_SECUNDARIO);
                lista.add(vacio);
            } else {
                for (int i = 0; i < visibles.size(); i++) {
                    lista.add(filaIntegrante(visibles.get(i)));
                    if (i < visibles.size() - 1) {
                        lista.add(Box.createVerticalStrut(12));
                    }
                }
            }
            lista.revalidate();
            lista.repaint();
        };
        mostrarInactivos.addActionListener(e -> repintar.accept(mostrarInactivos.isSelected()));
        repintar.accept(false);

        return panel;
    }

    private JPanel filaIntegrante(IntegranteDTO integrante) {
        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nombre = new JLabel(integrante.nombreCompleto());
        nombre.setFont(Estilos.TEXTO_NEGRITA);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(nombre);

        String estado = integrante.activo() ? "activo" : "inactivo";
        String desde = "desde " + Formato.anio(integrante.fechaIngreso());
        JLabel detalle = new JLabel(integrante.rol() + " \u00B7 " + desde + " \u00B7 " + estado);
        detalle.setFont(Estilos.TEXTO_PEQUENO);
        detalle.setForeground(integrante.activo() ? Estilos.TEXTO_SECUNDARIO : Estilos.CORAZON_INACTIVO);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(detalle);
        return fila;
    }

    private JPanel mensajeError(String mensaje) {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        JLabel label = new JLabel(mensaje != null ? mensaje : "No se pudo cargar el artista.");
        label.setFont(Estilos.TEXTO_NORMAL);
        label.setForeground(Estilos.TEXTO_SECUNDARIO);
        panel.add(label);
        return panel;
    }
}
