package com.equipo3.bibliotecamusical.negocio.servicios;

import com.equipo3.bibliotecamusical.daos.IAlbumDAO;
import com.equipo3.bibliotecamusical.daos.IArtistaDAO;
import com.equipo3.bibliotecamusical.dtos.AlbumDTO;
import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.dtos.CancionResultadoDTO;
import com.equipo3.bibliotecamusical.dtos.ResultadoBusqueda;
import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import com.equipo3.bibliotecamusical.negocio.mapeadores.AlbumMapper;
import com.equipo3.bibliotecamusical.negocio.mapeadores.ArtistaMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Buscador global de la biblioteca. Es el corazon de las tres pantallas de
 * consulta (Inicio, Artistas y Albumes): a partir de un texto libre y unos
 * filtros ({@link CriteriosBusqueda}) devuelve artistas, albumes y canciones
 * coincidentes, ya filtrados por tipo/genero/anio y ordenados.
 *
 * <p>Busca de forma amplia segun la estructura de la base de datos:
 * <ul>
 *   <li><b>Artistas:</b> por nombre del artista/banda, por nombre de alguno de
 *   sus integrantes o por genero.</li>
 *   <li><b>Albumes:</b> por nombre del album o por nombre de alguna de sus
 *   canciones.</li>
 *   <li><b>Canciones:</b> por nombre de la cancion (embebida en los albumes).</li>
 * </ul>
 */
public class BusquedaService {

    private final IArtistaDAO artistaDAO;
    private final IAlbumDAO albumDAO;

    public BusquedaService(IArtistaDAO artistaDAO, IAlbumDAO albumDAO) {
        this.artistaDAO = artistaDAO;
        this.albumDAO = albumDAO;
    }

    /**
     * Ejecuta la busqueda con los criterios dados.
     *
     * @param criterios texto + filtros; si es {@code null} se usan los criterios vacios (todo).
     * @return artistas, albumes y canciones que coinciden, ya filtrados y ordenados.
     */
    public ResultadoBusqueda buscar(CriteriosBusqueda criterios) {
        CriteriosBusqueda c = criterios != null ? criterios : CriteriosBusqueda.vacio();
        String texto = c.texto() == null ? "" : c.texto().trim();
        String textoLower = texto.toLowerCase();
        TipoContenido tipo = c.tipo() != null ? c.tipo() : TipoContenido.TODOS;
        OrdenBusqueda orden = c.orden() != null ? c.orden() : OrdenBusqueda.RELEVANCIA;
        Integer anio = c.anio();
        String genero = (c.genero() != null && !c.genero().isBlank()) ? c.genero().trim() : null;

        boolean quiereArtistas = tipo == TipoContenido.TODOS || tipo == TipoContenido.ARTISTA;
        boolean quiereAlbumes = tipo == TipoContenido.TODOS || tipo == TipoContenido.ALBUM;
        boolean quiereCanciones = tipo == TipoContenido.TODOS || tipo == TipoContenido.CANCION;

        // Mapa auxiliar para resolver nombre/imagen del artista dueño de cada album.
        Map<String, Artista> artistasPorId = new HashMap<>();
        for (Artista a : artistaDAO.listar()) {
            if (a.getId() != null) {
                artistasPorId.put(a.getId().toHexString(), a);
            }
        }

        List<ArtistaDTO> artistas = new ArrayList<>();
        List<AlbumDTO> albumes = new ArrayList<>();
        List<CancionResultadoDTO> canciones = new ArrayList<>();

        if (quiereArtistas) {
            List<Artista> encontrados = texto.isEmpty()
                    ? new ArrayList<>(artistasPorId.values())
                    : artistaDAO.buscarPorTexto(texto);
            for (Artista a : encontrados) {
                if (genero != null && !genero.equalsIgnoreCase(a.getGenero())) {
                    continue;
                }
                if (anio != null && (a.getFechaCreacion() == null || a.getFechaCreacion().getYear() != anio)) {
                    continue;
                }
                artistas.add(ArtistaMapper.aDTO(a));
            }
        }

        if (quiereAlbumes || quiereCanciones) {
            List<Album> encontrados = texto.isEmpty()
                    ? albumDAO.listar()
                    : albumDAO.buscarPorTexto(texto);
            for (Album al : encontrados) {
                String artistaHex = al.getArtistaId() != null ? al.getArtistaId().toHexString() : null;
                Artista art = artistaHex != null ? artistasPorId.get(artistaHex) : null;
                String artistaNombre = art != null ? art.getNombre() : "Artista desconocido";
                String artistaId = art != null && art.getId() != null ? art.getId().toHexString() : null;

                boolean coincideGeneroAlbum = genero == null || genero.equalsIgnoreCase(al.getGenero());
                boolean coincideAnio = anio == null
                        || (al.getFechaLanzamiento() != null && al.getFechaLanzamiento().getYear() == anio);
                boolean coincideNombreAlbum = texto.isEmpty()
                        || (al.getNombre() != null && al.getNombre().toLowerCase().contains(textoLower));

                if (quiereAlbumes && coincideGeneroAlbum && coincideAnio && coincideNombreAlbum) {
                    albumes.add(AlbumMapper.aDTO(al));
                }

                if (quiereCanciones && al.getCanciones() != null) {
                    for (Cancion cancion : al.getCanciones()) {
                        boolean coincideNombreCancion = texto.isEmpty()
                                || (cancion.getNombre() != null
                                && cancion.getNombre().toLowerCase().contains(textoLower));
                        boolean coincideGeneroCancion = genero == null
                                || genero.equalsIgnoreCase(cancion.getGenero())
                                || genero.equalsIgnoreCase(al.getGenero());
                        if (coincideNombreCancion && coincideGeneroCancion && coincideAnio) {
                            canciones.add(new CancionResultadoDTO(
                                    cancion.getId() != null ? cancion.getId().toHexString() : null,
                                    cancion.getNombre(),
                                    cancion.getNumeroPista(),
                                    cancion.getDuracionSegundos(),
                                    cancion.getGenero(),
                                    al.getId() != null ? al.getId().toHexString() : null,
                                    al.getNombre(),
                                    al.getImagenPortada(),
                                    artistaId,
                                    artistaNombre,
                                    al.getFechaLanzamiento()));
                        }
                    }
                }
            }
        }

        ordenar(artistas, albumes, canciones, orden, textoLower);
        return new ResultadoBusqueda(artistas, albumes, canciones);
    }

    // ------------------------------------------------------------------
    // Ordenamiento
    // ------------------------------------------------------------------

    private void ordenar(List<ArtistaDTO> artistas, List<AlbumDTO> albumes,
            List<CancionResultadoDTO> canciones, OrdenBusqueda orden, String textoLower) {
        switch (orden) {
            case ALFABETICO_AZ -> {
                artistas.sort(Comparator.comparing(a -> nombreClave(a.nombre())));
                albumes.sort(Comparator.comparing(a -> nombreClave(a.nombre())));
                canciones.sort(Comparator.comparing(c -> nombreClave(c.nombre())));
            }
            case ALFABETICO_ZA -> {
                artistas.sort(Comparator.comparing((ArtistaDTO a) -> nombreClave(a.nombre())).reversed());
                albumes.sort(Comparator.comparing((AlbumDTO a) -> nombreClave(a.nombre())).reversed());
                canciones.sort(Comparator.comparing((CancionResultadoDTO c) -> nombreClave(c.nombre())).reversed());
            }
            case ANIO_RECIENTE -> {
                artistas.sort(Comparator.comparingInt((ArtistaDTO a) -> anio(a.fechaCreacion())).reversed());
                albumes.sort(Comparator.comparingInt((AlbumDTO a) -> anio(a.fechaLanzamiento())).reversed());
                canciones.sort(Comparator.comparingInt((CancionResultadoDTO c) -> anio(c.fechaLanzamiento())).reversed());
            }
            case ANIO_ANTIGUO -> {
                artistas.sort(Comparator.comparingInt(a -> anio(a.fechaCreacion())));
                albumes.sort(Comparator.comparingInt(a -> anio(a.fechaLanzamiento())));
                canciones.sort(Comparator.comparingInt(c -> anio(c.fechaLanzamiento())));
            }
            case RELEVANCIA -> {
                if (textoLower.isEmpty()) {
                    // Sin texto: los mas recientes primero para albumes/canciones; artistas por nombre.
                    artistas.sort(Comparator.comparing(a -> nombreClave(a.nombre())));
                    albumes.sort(Comparator.comparingInt((AlbumDTO a) -> anio(a.fechaLanzamiento())).reversed());
                    canciones.sort(Comparator.comparingInt((CancionResultadoDTO c) -> anio(c.fechaLanzamiento())).reversed());
                } else {
                    artistas.sort(Comparator.comparingInt((ArtistaDTO a) -> relevancia(a.nombre(), textoLower))
                            .thenComparing(a -> nombreClave(a.nombre())));
                    albumes.sort(Comparator.comparingInt((AlbumDTO a) -> relevancia(a.nombre(), textoLower))
                            .thenComparing(a -> nombreClave(a.nombre())));
                    canciones.sort(Comparator.comparingInt((CancionResultadoDTO c) -> relevancia(c.nombre(), textoLower))
                            .thenComparing(c -> nombreClave(c.nombre())));
                }
            }
        }
    }

    /** Menor valor = mas relevante: 0 coincidencia exacta, 1 empieza con, 2 contiene, 3 sin match directo. */
    private static int relevancia(String nombre, String textoLower) {
        if (nombre == null) {
            return 3;
        }
        String n = nombre.toLowerCase();
        if (n.equals(textoLower)) {
            return 0;
        }
        if (n.startsWith(textoLower)) {
            return 1;
        }
        if (n.contains(textoLower)) {
            return 2;
        }
        return 3;
    }

    private static String nombreClave(String nombre) {
        return nombre == null ? "" : nombre.toLowerCase();
    }

    private static int anio(LocalDate fecha) {
        return fecha == null ? Integer.MIN_VALUE : fecha.getYear();
    }
}
