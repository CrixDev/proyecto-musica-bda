package com.equipo3.bibliotecamusical.daos;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.equipo3.bibliotecamusical.entidades.Artista;
import com.equipo3.bibliotecamusical.entidades.Integrante;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.persistencia.Colaciones;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.types.ObjectId;

/** Implementacion MongoDB del repositorio de artistas. */
public class ArtistaDAOImpl implements IArtistaDAO {

    private final MongoCollection<Artista> coleccion;

    public ArtistaDAOImpl(MongoDatabase db) {
        this.coleccion = db.getCollection("artistas", Artista.class);
    }

    @Override
    public Artista crear(Artista artista) {
        return OperacionesMongo.ejecutar(() -> {
            InsertOneResult r = coleccion.insertOne(artista);
            if (artista.getId() == null && r.getInsertedId() != null) {
                artista.setId(r.getInsertedId().asObjectId().getValue());
            }
            return artista;
        });
    }

    @Override
    public Optional<Artista> buscarPorId(ObjectId id) {
        return OperacionesMongo.ejecutar(
                () -> Optional.ofNullable(coleccion.find(eq("_id", id)).first()));
    }

    @Override
    public List<Artista> listar() {
        return OperacionesMongo.ejecutar(
                () -> coleccion.find().into(new ArrayList<>()));
    }

    @Override
    public List<Artista> listarPorTipo(TipoArtista tipo) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.find(eq("tipo", tipo.getClave())).into(new ArrayList<>()));
    }

    @Override
    public List<Artista> listarPorGenero(String genero) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.find(eq("genero", genero)).into(new ArrayList<>()));
    }

    @Override
    public List<Artista> buscarPorNombre(String texto) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.find(Filters.regex("nombre", Pattern.quote(texto), "i"))
                        .into(new ArrayList<>()));
    }

    @Override
    public List<Artista> buscarPorTexto(String texto) {
        return OperacionesMongo.ejecutar(() -> {
            String patron = Pattern.quote(texto);
            return coleccion.find(Filters.or(
                    Filters.regex("nombre", patron, "i"),
                    Filters.regex("integrantes.nombreCompleto", patron, "i"),
                    Filters.regex("genero", patron, "i")))
                    .into(new ArrayList<>());
        });
    }

    @Override
    public boolean existePorNombreYTipo(String nombre, TipoArtista tipo) {
        return OperacionesMongo.ejecutar(() -> coleccion.countDocuments(
                and(eq("nombre", nombre), eq("tipo", tipo.getClave())),
                new CountOptions().collation(Colaciones.INSENSIBLE)) > 0);
    }

    @Override
    public Artista actualizar(Artista artista) {
        return OperacionesMongo.ejecutar(() -> {
            coleccion.replaceOne(eq("_id", artista.getId()), artista);
            return artista;
        });
    }

    @Override
    public boolean eliminar(ObjectId id) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.deleteOne(eq("_id", id)).getDeletedCount() > 0);
    }

    @Override
    public long contar() {
        return OperacionesMongo.ejecutar(() -> coleccion.countDocuments());
    }

    @Override
    public void agregarIntegrante(ObjectId artistaId, Integrante integrante) {
        OperacionesMongo.ejecutar(
                () -> coleccion.updateOne(eq("_id", artistaId), Updates.push("integrantes", integrante)));
    }

    @Override
    public void eliminarIntegrante(ObjectId artistaId, String nombreCompleto) {
        OperacionesMongo.ejecutar(() -> coleccion.updateOne(
                eq("_id", artistaId),
                Updates.pull("integrantes", new Document("nombreCompleto", nombreCompleto))));
    }
    @Override
    public void crearMuchos(List<Artista> artistas) {
        if (artistas == null || artistas.isEmpty()) {
            return;
        }
        OperacionesMongo.ejecutar(() -> {
            coleccion.insertMany(artistas);
            return null; // OperacionesMongo.ejecutar espera un retorno; devolvemos null al ser void
        });
    }
}
