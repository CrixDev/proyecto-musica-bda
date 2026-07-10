package com.equipo3.bibliotecamusical.presentacion.vistas;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.CancionResultadoDTO;
import com.equipo3.bibliotecamusical.dtos.ResultadoBusqueda;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.negocio.servicios.CriteriosBusqueda;
import com.equipo3.bibliotecamusical.presentacion.componentes.BarraBusqueda;
import com.equipo3.bibliotecamusical.presentacion.componentes.BarraPaginacion;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonPildora;
import com.equipo3.bibliotecamusical.presentacion.componentes.CirculoArtista;
import com.equipo3.bibliotecamusical.presentacion.componentes.PanelFiltros;
import com.equipo3.bibliotecamusical.presentacion.componentes.TarjetaAlbum;
import com.equipo3.bibliotecamusical.presentacion.componentes.WrapLayout;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import com.equipo3.bibliotecamusical.presentacion.estilo.Formato;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Pantalla de Inicio: buscador global + secciones "Artistas", "Álbumes" y
 * "Canciones". El buscador cubre nombre de artista/banda, nombre de integrantes,
 * nombre de álbum y nombre de canción, con filtros por tipo, orden, año y género
 * (a través de {@link com.equipo3.bibliotecamusical.negocio.servicios.BusquedaService}).
 */
public class PantallaInicio extends JPanel {

    private static final int LADO_ARTISTA = 128;
    private static final int LADO_PORTADA = 150;
    private static final int TAMANO_PAGINA = 12;

    private final Servicios servicios;
    private final NavegacionVistas navegacion;

    private final JPanel contenido = new JPanel();
    private final BarraBusqueda barra = new BarraBusqueda("Buscar artistas, álbumes o canciones...");
    private BotonPildora botonFiltros;

    private CriteriosBusqueda criterios = CriteriosBusqueda.vacio();
    // Página actual de cada sección (paginación independiente por sección).
    private int pagArtistas = 0;
    private int pagAlbumes = 0;
    private int pagCanciones = 0;

    public PantallaInicio(Servicios servicios, NavegacionVistas navegacion) {
        this.servicios = servicios;
        this.navegacion = navegacion != null ? navegacion : NavegacionVistas.NULA;
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);

        add(construirEncabezado(), BorderLayout.NORTH);

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

    private JPanel construirEncabezado() {
        JPanel barraSup = new JPanel(new BorderLayout(12, 0));
        barraSup.setBackground(Estilos.FONDO);
        barraSup.setBorder(BorderFactory.createEmptyBorder(18, 32, 8, 32));

        barra.alEscribir(texto -> {
            criterios = criterios.conTexto(texto);
            reiniciarPaginas();
            recargar();
        });
        barraSup.add(barra, BorderLayout.CENTER);

        botonFiltros = new BotonPildora("⚙  Filtros", false);
        botonFiltros.addActionListener(e -> PanelFiltros.mostrar(
                botonFiltros, criterios, aniosDisponibles(), true, nuevos -> {
                    criterios = nuevos.conTexto(barra.getTexto());
                    reiniciarPaginas();
                    recargar();
                }));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(botonFiltros);
        barraSup.add(derecha, BorderLayout.EAST);
        return barraSup;
    }

    /** Coloca el foco en la barra de búsqueda (usado por la opción "Buscar" del menú). */
    public void enfocarBusqueda() {
        barra.enfocar();
    }

    /** Vuelve a consultar con los criterios actuales y repinta las secciones. */
    public void recargar() {
        contenido.removeAll();
        ResultadoBusqueda resultado = servicios.busqueda().buscar(criterios);
        boolean buscando = criterios.texto() != null && !criterios.texto().isBlank();

        if (resultado.vacio()) {
            contenido.add(mensajeVacio(buscando));
        } else {
            if (!resultado.artistas().isEmpty()) {
                contenido.add(seccionArtistas("Artistas", resultado.artistas()));
            }
            if (!resultado.albumes().isEmpty()) {
                contenido.add(seccionAlbumes(buscando ? "Álbumes" : "Álbumes recientes", resultado.albumes()));
            }
            if (!resultado.canciones().isEmpty()) {
                contenido.add(seccionCanciones("Canciones", resultado.canciones()));
            }
        }
        contenido.revalidate();
        contenido.repaint();
    }

    // ------------------------------------------------------------- secciones

    private JPanel seccionArtistas(String titulo, List<ArtistaDTO> artistas) {
        int total = artistas.size();
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAMANO_PAGINA));
        pagArtistas = Math.max(0, Math.min(pagArtistas, totalPaginas - 1));
        int desde = pagArtistas * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, total);
        JPanel grid = grid();
        for (int i = desde; i < hasta; i++) {
            ArtistaDTO a = artistas.get(i);
            grid.add(new CirculoArtista(a, LADO_ARTISTA, () -> navegacion.irADetalleArtista(a.id())));
        }
        return seccion(titulo, total, grid, pagArtistas, totalPaginas, p -> {
            pagArtistas = p;
            recargar();
        });
    }

    private JPanel seccionAlbumes(String titulo, List<AlbumDTO> albumes) {
        int total = albumes.size();
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAMANO_PAGINA));
        pagAlbumes = Math.max(0, Math.min(pagAlbumes, totalPaginas - 1));
        int desde = pagAlbumes * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, total);
        JPanel grid = grid();
        for (int i = desde; i < hasta; i++) {
            AlbumDTO al = albumes.get(i);
            String sub = Formato.anio(al.fechaLanzamiento());
            grid.add(new TarjetaAlbum(al.imagenPortada(), al.nombre(), sub, LADO_PORTADA,
                    () -> navegacion.irADetalleAlbum(al.id())));
        }
        return seccion(titulo, total, grid, pagAlbumes, totalPaginas, p -> {
            pagAlbumes = p;
            recargar();
        });
    }

    private JPanel seccionCanciones(String titulo, List<CancionResultadoDTO> canciones) {
        int total = canciones.size();
        int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAMANO_PAGINA));
        pagCanciones = Math.max(0, Math.min(pagCanciones, totalPaginas - 1));
        int desde = pagCanciones * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, total);
        JPanel grid = grid();
        for (int i = desde; i < hasta; i++) {
            CancionResultadoDTO c = canciones.get(i);
            grid.add(new TarjetaAlbum(c.imagenPortada(), c.nombre(), c.artistaNombre(), LADO_PORTADA,
                    () -> navegacion.irADetalleAlbum(c.albumId())));
        }
        return seccion(titulo, total, grid, pagCanciones, totalPaginas, p -> {
            pagCanciones = p;
            recargar();
        });
    }

    // -------------------------------------------------------------- ayudas

    private void reiniciarPaginas() {
        pagArtistas = 0;
        pagAlbumes = 0;
        pagCanciones = 0;
    }

    private JPanel grid() {
        JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 6, 6));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        return grid;
    }

    private JPanel seccion(String titulo, int total, JPanel grid,
            int paginaActual, int totalPaginas, IntConsumer alIrAPagina) {
        JPanel seccion = new JPanel();
        seccion.setOpaque(false);
        seccion.setLayout(new BoxLayout(seccion, BoxLayout.Y_AXIS));
        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(titulo + "  (" + total + ")");
        lbl.setFont(Estilos.TITULO_MEDIANO);
        lbl.setForeground(Estilos.TEXTO_PRIMARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(16, 6, 8, 0));
        seccion.add(lbl);
        seccion.add(grid);

        if (totalPaginas > 1) {
            JPanel filaPag = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            filaPag.setOpaque(false);
            filaPag.setAlignmentX(Component.LEFT_ALIGNMENT);
            filaPag.add(new BarraPaginacion(paginaActual, totalPaginas, total, alIrAPagina));
            seccion.add(filaPag);
        }
        return seccion;
    }

    private JPanel mensajeVacio(boolean buscando) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
        JLabel lbl = new JLabel(buscando
                ? "No encontramos resultados para tu búsqueda."
                : "Aún no hay contenido. Usa \"Inserción masiva\" en Artistas para cargar datos.");
        lbl.setFont(Estilos.TEXTO_NORMAL);
        lbl.setForeground(Estilos.TEXTO_SECUNDARIO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lbl);
        return panel;
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
