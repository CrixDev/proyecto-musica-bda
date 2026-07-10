package com.equipo3.bibliotecamusical.presentacion.vistas;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.ResultadoBusqueda;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.negocio.servicios.CriteriosBusqueda;
import com.equipo3.bibliotecamusical.negocio.servicios.TipoContenido;
import com.equipo3.bibliotecamusical.presentacion.componentes.BarraBusqueda;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonPildora;
import com.equipo3.bibliotecamusical.presentacion.componentes.CirculoArtista;
import com.equipo3.bibliotecamusical.presentacion.componentes.PanelFiltros;
import com.equipo3.bibliotecamusical.presentacion.componentes.WrapLayout;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

/**
 * Pantalla de Artistas: buscador por nombre de artista/banda, por nombre de
 * integrante o por género, con filtros (orden, año, género) y botón de
 * "Inserción masiva". Los resultados se muestran como un grid de círculos.
 */
public class PantallaArtistas extends JPanel {

    private static final int LADO_ARTISTA = 128;
    private static final Color NEGRO = new Color(0x18, 0x18, 0x1F);

    private final Servicios servicios;
    private final NavegacionVistas navegacion;

    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 16));
    private final BarraBusqueda barra = new BarraBusqueda("Buscar por nombre o género");
    private BotonPildora botonFiltros;

    private CriteriosBusqueda criterios = CriteriosBusqueda.vacio().conTipo(TipoContenido.ARTISTA);

    public PantallaArtistas(Servicios servicios, NavegacionVistas navegacion) {
        this.servicios = servicios;
        this.navegacion = navegacion != null ? navegacion : NavegacionVistas.NULA;
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);

        add(construirEncabezado(), BorderLayout.NORTH);

        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 26, 24, 26));

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(Estilos.FONDO);
        JLabel titulo = new JLabel("Artistas");
        titulo.setFont(Estilos.TITULO_GRANDE);
        titulo.setForeground(Estilos.TEXTO_PRIMARIO);
        titulo.setBorder(BorderFactory.createEmptyBorder(4, 32, 4, 32));
        cuerpo.add(titulo, BorderLayout.NORTH);
        cuerpo.add(grid, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(cuerpo);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Estilos.FONDO);
        add(scroll, BorderLayout.CENTER);

        recargar();
    }

    private JPanel construirEncabezado() {
        JPanel barraSup = new JPanel(new BorderLayout(12, 0));
        barraSup.setBackground(Estilos.FONDO);
        barraSup.setBorder(BorderFactory.createEmptyBorder(18, 32, 8, 32));

        barra.alEscribir(texto -> {
            criterios = criterios.conTexto(texto);
            recargar();
        });
        barraSup.add(barra, BorderLayout.CENTER);

        botonFiltros = new BotonPildora("⚙  Filtros", false);
        botonFiltros.addActionListener(e -> PanelFiltros.mostrar(
                botonFiltros, criterios, aniosDisponibles(), false, nuevos -> {
                    criterios = nuevos.conTexto(barra.getTexto()).conTipo(TipoContenido.ARTISTA);
                    recargar();
                }));

        JButton insercion = botonOscuro("+  Inserción masiva");
        insercion.addActionListener(e -> ejecutarInsercionMasiva());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(botonFiltros);
        derecha.add(insercion);
        barraSup.add(derecha, BorderLayout.EAST);
        return barraSup;
    }

    /** Repinta el grid con los artistas que coinciden con el buscador/filtros. */
    public void recargar() {
        grid.removeAll();
        ResultadoBusqueda resultado = servicios.busqueda().buscar(criterios);
        List<ArtistaDTO> artistas = resultado.artistas();
        if (artistas.isEmpty()) {
            JLabel vacio = new JLabel(criterios.texto() != null && !criterios.texto().isBlank()
                    ? "No hay artistas que coincidan con \"" + criterios.texto() + "\"."
                    : "No hay artistas registrados. Usa \"Inserción masiva\" para cargar datos.");
            vacio.setFont(Estilos.TEXTO_NORMAL);
            vacio.setForeground(Estilos.TEXTO_SECUNDARIO);
            grid.add(vacio);
        } else {
            for (ArtistaDTO a : artistas) {
                grid.add(new CirculoArtista(a, LADO_ARTISTA, () -> navegacion.irADetalleArtista(a.id())));
            }
        }
        grid.revalidate();
        grid.repaint();
    }

    private void ejecutarInsercionMasiva() {
        int r = JOptionPane.showConfirmDialog(this,
                "Se cargarán 30 artistas (15 solistas y 15 bandas) con sus álbumes desde artistas.json.\n"
                + "¿Deseas continuar?",
                "Inserción masiva", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<Void, Void>() {
            private String error;

            @Override
            protected Void doInBackground() {
                try {
                    servicios.cargaMasiva().ejecutarCarga();
                } catch (RuntimeException ex) {
                    error = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                if (error != null) {
                    JOptionPane.showMessageDialog(PantallaArtistas.this,
                            "No se pudo completar la carga:\n" + error,
                            "Inserción masiva", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(PantallaArtistas.this,
                            "¡Carga masiva completada!",
                            "Inserción masiva", JOptionPane.INFORMATION_MESSAGE);
                }
                recargar();
            }
        }.execute();
    }

    private JButton botonOscuro(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x33, 0x33, 0x40) : NEGRO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(Estilos.TEXTO_NEGRITA);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 22));
        btn.setAlignmentY(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private List<Integer> aniosDisponibles() {
        return servicios.albumes().listar().stream()
                .map(AlbumDTO::fechaLanzamiento)
                .filter(f -> f != null)
                .map(java.time.LocalDate::getYear)
                .distinct()
                .sorted(java.util.Comparator.reverseOrder())
                .toList();
    }
}
