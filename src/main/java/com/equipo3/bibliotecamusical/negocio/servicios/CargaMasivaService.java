/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.negocio.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.equipo3.bibliotecamusical.daos.IAlbumDAO;
import com.equipo3.bibliotecamusical.entidades.*;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import com.equipo3.bibliotecamusical.negocio.excepciones.DuplicadoException;
import org.bson.types.ObjectId;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CargaMasivaService {

    private final ArtistaService artistaService;
    private final IAlbumDAO albumDAO;

    public CargaMasivaService(ArtistaService artistaService, IAlbumDAO albumDAO) {
        this.artistaService = artistaService;
        this.albumDAO = albumDAO;
    }

    public void ejecutarCarga() {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            InputStream is = getClass().getResourceAsStream("/artistas.json");
            if (is == null) {
                throw new ValidacionException("No se encontró el archivo artistas.json");
            }

            JsonNode rootArray = mapper.readTree(is);
            if (!rootArray.isArray() || rootArray.size() < 30) {
                throw new ValidacionException("El archivo JSON debe contener exactamente 30 registros.");
            }

            List<Artista> listaArtistas = new ArrayList<>();

            // 1. Parsear y Validar la estructura del JSON de forma independiente
            for (JsonNode nodoArtista : rootArray) {
                Artista artista = new Artista();
                artista.setNombre(nodoArtista.get("nombre").asText());
                artista.setTipo(TipoArtista.valueOf(nodoArtista.get("tipo").asText().toUpperCase()));
                artista.setFechaCreacion(LocalDate.now());

                artista.setGenero(nodoArtista.get("genero").asText());
                // Mapear integrantes si es BANDA
                List<Integrante> integrantes = new ArrayList<>();
                if (nodoArtista.has("integrantes") && nodoArtista.get("integrantes").isArray()) {
                    for (JsonNode nodoInt : nodoArtista.get("integrantes")) {
                        Integrante integrante = new Integrante(
                                nodoInt.get("nombreCompleto").asText(),
                                nodoInt.get("rol").asText(),
                                LocalDate.parse(nodoInt.get("fechaIngreso").asText()),
                                nodoInt.has("fechaSalida") ? LocalDate.parse(nodoInt.get("fechaSalida").asText()) : null,
                                nodoInt.get("activo").asBoolean()
                        );
                        integrantes.add(integrante);
                    }
                }
                // Si tu entidad maneja la lista de integrantes, asígnala aquí: artista.setIntegrantes(integrantes);

                artista.setIntegrantes(integrantes);
                // Validar que el JSON declare al menos 2 álbumes y cada uno 3 canciones
                JsonNode albumesNodo = nodoArtista.get("albumes");
                if (albumesNodo == null || !albumesNodo.isArray() || albumesNodo.size() < 2) {
                    throw new ValidacionException("Cada artista en el JSON debe tener al menos 2 álbumes. Error en: " + artista.getNombre());
                }

                for (JsonNode nodoAlbum : albumesNodo) {
                    JsonNode cancionesNodo = nodoAlbum.get("canciones");
                    if (cancionesNodo == null || !cancionesNodo.isArray() || cancionesNodo.size() < 3) {
                        throw new ValidacionException("Cada álbum debe tener al menos 3 canciones. Error en: " + nodoAlbum.get("nombre").asText());
                    }
                }

                listaArtistas.add(artista);
            }

            // 2. Guardar los artistas de un solo golpe (Garantiza consistencia mitad/mitad y tamaño)
            artistaService.insertarLoteArtistas(listaArtistas);

            // 3. Insertar los álbumes correspondientes en cascada usando los IDs generados
            for (int i = 0; i < listaArtistas.size(); i++) {
                Artista artistaInsertado = listaArtistas.get(i);
                JsonNode nodoArtistaOriginal = rootArray.get(i);

                for (JsonNode nodoAlbum : nodoArtistaOriginal.get("albumes")) {
                    // Creamos la entidad Album nativa directamente sin usar DTOs compartidos
                    Album album = new Album();
                    album.setId(null);
                    album.setArtistaId(artistaInsertado.getId()); // Vinculamos con el ID real que MongoDB acaba de generar
                    album.setNombre(nodoAlbum.get("nombre").asText());
                    album.setGenero(nodoAlbum.get("genero").asText());
                    album.setFechaLanzamiento(LocalDate.parse(nodoAlbum.get("fechaLanzamiento").asText()));
                    album.setImagenPortada(nodoAlbum.get("imagenPortada").asText());

                    // Procesamos las canciones embebidas requeridas (mínimo 3 por álbum)
                    List<Cancion> canciones = new ArrayList<>();
                    for (JsonNode nodoCancion : nodoAlbum.get("canciones")) {
                        Cancion cancion = new Cancion(
                                new ObjectId(), // Forzamos su ID único de Mongo para subdocumentos
                                nodoCancion.get("nombre").asText(),
                                nodoCancion.get("numeroPista").asInt(),
                                nodoCancion.has("duracionSegundos") ? nodoCancion.get("duracionSegundos").asInt() : 180,
                                nodoCancion.get("genero").asText()
                        );
                        canciones.add(cancion);
                    }
                    album.setCanciones(canciones);

                    // Guardamos el álbum usando el DAO directamente
                    if (albumDAO.existeAlbumDeArtista(album.getArtistaId(), album.getNombre())) {
                        throw new DuplicadoException("El álbum '" + album.getNombre() + "' ya existe para este artista.");
                    }
                    albumDAO.crear(album); // Tu albumDAO se encarga de la persistencia nativa
                }
            }
            System.out.println("¡Inserción masiva completada exitosamente sin alterar DTOs del equipo!");

        } catch (Exception e) {
            throw new ValidacionException("Fallo catastrófico en la carga masiva: " + e.getMessage());
        }
    }
}
