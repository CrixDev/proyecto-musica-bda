package com.equipo3.bibliotecamusical.presentacion.vistas;

import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.ResultadoBusqueda;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.negocio.servicios.CriteriosBusqueda;
import com.equipo3.bibliotecamusical.negocio.servicios.TipoContenido;
import com.equipo3.bibliotecamusical.presentacion.componentes.BarraBusqueda;
import com.equipo3.bibliotecamusical.presentacion.componentes.BarraPaginacion;
import com.equipo3.bibliotecamusical.presentacion.componentes.BotonPildora;
import com.equipo3.bibliotecamusical.presentacion.componentes.CirculoArtista;
import com.equipo3.bibliotecamusical.presentacion.componentes.PanelFiltros;
import com.equipo3.bibliotecamusical.presentacion.componentes.WrapLayout;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Pantalla de Artistas: buscador por nombre de artista/banda, por nombre de
 * integrante o por género, con filtros (orden, año, género) y paginación. Los
 * resultados se muestran como un grid de círculos. Los datos se cargan
 * automáticamente al iniciar la aplicación (ver {@code App}).
 */
public class PantallaArtistas extends JPanel {

    private static final int LADO_ARTISTA = 128;
    private static final int TAMANO_PAGINA = 12;

    private final Servicios servicios;
    private final NavegacionVistas navegacion;

    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 16));
    private final JPanel paginador = new JPanel(new BorderLayout());
    private final BarraBusqueda barra = new BarraBusqueda("Buscar por nombre o género");
    private BotonPildora botonFiltros;

    private CriteriosBusqueda criterios = CriteriosBusqueda.vacio().conTipo(TipoContenido.ARTISTA);
    private int pagina = 0;

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

        paginador.setBackground(Estilos.FONDO);
        paginador.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.BORDE));
        add(paginador, BorderLayout.SOUTH);

        recargar();
    }

    private JPanel construirEncabezado() {
        JPanel barraSup = new JPanel(new BorderLayout(12, 0));
        barraSup.setBackground(Estilos.FONDO);
        barraSup.setBorder(BorderFactory.createEmptyBorder(18, 32, 8, 32));

        barra.alEscribir(texto -> {
            criterios = criterios.conTexto(texto);
            pagina = 0;
            recargar();
        });
        barraSup.add(barra, BorderLayout.CENTER);

        botonFiltros = new BotonPildora("⚙  Filtros", false);
        botonFiltros.addActionListener(e -> PanelFiltros.mostrar(
                botonFiltros, criterios, aniosDisponibles(), false, nuevos -> {
                    criterios = nuevos.conTexto(barra.getTexto()).conTipo(TipoContenido.ARTISTA);
                    pagina = 0;
                    recargar();
                }));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(botonFiltros);
        barraSup.add(derecha, BorderLayout.EAST);
        return barraSup;
    }

    /** Repinta el grid con la página actual de artistas que coinciden con el buscador/filtros. */
    public void recargar() {
        grid.removeAll();
        paginador.removeAll();
        ResultadoBusqueda resultado = servicios.busqueda().buscar(criterios);
        List<ArtistaDTO> artistas = resultado.artistas();
        int total = artistas.size();

        if (artistas.isEmpty()) {
            JLabel vacio = new JLabel(criterios.texto() != null && !criterios.texto().isBlank()
                    ? "No hay artistas que coincidan con \"" + criterios.texto() + "\"."
                    : "No hay artistas registrados todavía.");
            vacio.setFont(Estilos.TEXTO_NORMAL);
            vacio.setForeground(Estilos.TEXTO_SECUNDARIO);
            grid.add(vacio);
        } else {
            int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAMANO_PAGINA));
            pagina = Math.max(0, Math.min(pagina, totalPaginas - 1));
            int desde = pagina * TAMANO_PAGINA;
            int hasta = Math.min(desde + TAMANO_PAGINA, total);
            for (int i = desde; i < hasta; i++) {
                ArtistaDTO a = artistas.get(i);
                grid.add(new CirculoArtista(a, LADO_ARTISTA, () -> navegacion.irADetalleArtista(a.id())));
            }
            if (totalPaginas > 1) {
                paginador.add(new BarraPaginacion(pagina, totalPaginas, total, p -> {
                    pagina = p;
                    recargar();
                }), BorderLayout.CENTER);
            }
        }
        grid.revalidate();
        grid.repaint();
        paginador.revalidate();
        paginador.repaint();
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
