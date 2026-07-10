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
import com.equipo3.bibliotecamusical.presentacion.componentes.PanelFiltros;
import com.equipo3.bibliotecamusical.presentacion.componentes.TarjetaAlbum;
import com.equipo3.bibliotecamusical.presentacion.componentes.WrapLayout;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Pantalla de Álbumes: buscador por nombre de álbum o por nombre de una de sus
 * canciones, con filtros (orden, año, género). Los resultados se muestran como
 * un grid de portadas con el nombre del álbum y su artista.
 */
public class PantallaAlbumes extends JPanel {

    private static final int LADO_PORTADA = 160;
    private static final int TAMANO_PAGINA = 12;

    private final Servicios servicios;
    private final NavegacionVistas navegacion;

    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 16));
    private final JPanel paginador = new JPanel(new BorderLayout());
    private final BarraBusqueda barra = new BarraBusqueda("Buscar álbum por nombre o canción");
    private BotonPildora botonFiltros;

    private CriteriosBusqueda criterios = CriteriosBusqueda.vacio().conTipo(TipoContenido.ALBUM);
    private int pagina = 0;

    public PantallaAlbumes(Servicios servicios, NavegacionVistas navegacion) {
        this.servicios = servicios;
        this.navegacion = navegacion != null ? navegacion : NavegacionVistas.NULA;
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);

        add(construirEncabezado(), BorderLayout.NORTH);

        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 26, 24, 26));

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(Estilos.FONDO);
        JLabel titulo = new JLabel("Álbumes");
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

        botonFiltros = new BotonPildora("≡  Filtros", true);
        botonFiltros.addActionListener(e -> PanelFiltros.mostrar(
                botonFiltros, criterios, aniosDisponibles(), false, nuevos -> {
                    criterios = nuevos.conTexto(barra.getTexto()).conTipo(TipoContenido.ALBUM);
                    pagina = 0;
                    recargar();
                }));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(botonFiltros);
        barraSup.add(derecha, BorderLayout.EAST);
        return barraSup;
    }

    /** Repinta el grid con la página actual de álbumes que coinciden con el buscador/filtros. */
    public void recargar() {
        grid.removeAll();
        paginador.removeAll();
        Map<String, String> nombreArtistaPorId = mapaArtistas();
        ResultadoBusqueda resultado = servicios.busqueda().buscar(criterios);
        List<AlbumDTO> albumes = resultado.albumes();
        int total = albumes.size();

        if (albumes.isEmpty()) {
            JLabel vacio = new JLabel(criterios.texto() != null && !criterios.texto().isBlank()
                    ? "No hay álbumes que coincidan con \"" + criterios.texto() + "\"."
                    : "No hay álbumes registrados todavía.");
            vacio.setFont(Estilos.TEXTO_NORMAL);
            vacio.setForeground(Estilos.TEXTO_SECUNDARIO);
            grid.add(vacio);
        } else {
            int totalPaginas = Math.max(1, (int) Math.ceil(total / (double) TAMANO_PAGINA));
            pagina = Math.max(0, Math.min(pagina, totalPaginas - 1));
            int desde = pagina * TAMANO_PAGINA;
            int hasta = Math.min(desde + TAMANO_PAGINA, total);
            for (int i = desde; i < hasta; i++) {
                AlbumDTO al = albumes.get(i);
                String artista = nombreArtistaPorId.getOrDefault(al.artistaId(), "");
                grid.add(new TarjetaAlbum(al.imagenPortada(), al.nombre(), artista, LADO_PORTADA,
                        () -> navegacion.irADetalleAlbum(al.id())));
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

    private Map<String, String> mapaArtistas() {
        Map<String, String> mapa = new HashMap<>();
        for (ArtistaDTO a : servicios.artistas().listar()) {
            mapa.put(a.id(), a.nombre());
        }
        return mapa;
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
