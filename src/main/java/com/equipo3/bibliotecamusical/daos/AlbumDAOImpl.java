package com.equipo3.bibliotecamusical.daos;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.equipo3.bibliotecamusical.entidades.Album;
import com.equipo3.bibliotecamusical.entidades.Cancion;
import com.equipo3.bibliotecamusical.persistencia.Colaciones;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.types.ObjectId;

/** Implementacion MongoDB del repositorio de albumes (canciones embebidas). */
public class AlbumDAOImpl implements IAlbumDAO {

    private final MongoCollection<Album> coleccion;

    public AlbumDAOImpl(MongoDatabase db) {
        this.coleccion = db.getCollection("albumes", Album.class);
    }

    @Override
    public Album crear(Album album) {
        return OperacionesMongo.ejecutar(() -> {
            InsertOneResult r = coleccion.insertOne(album);
            if (album.getId() == null && r.getInsertedId() != null) {
                album.setId(r.getInsertedId().asObjectId().getValue());
            }
            return album;
        });
    }

    @Override
    public void insertarVarios(List<Album> albumes) {
        if (albumes.isEmpty()) {
            return;
        }
        OperacionesMongo.ejecutar(
                () -> coleccion.insertMany(albumes, new InsertManyOptions().ordered(false)));
    }

    @Override
    public Optional<Album> buscarPorId(ObjectId id) {
        return OperacionesMongo.ejecutar(
                () -> Optional.ofNullable(coleccion.find(eq("_id", id)).first()));
    }

    @Override
    public List<Album> listar() {
        return OperacionesMongo.ejecutar(() -> coleccion.find().into(new ArrayList<>()));
    }

    @Override
    public List<Album> listarPorArtista(ObjectId artistaId) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.find(eq("artistaId", artistaId)).into(new ArrayList<>()));
    }

    @Override
    public List<Album> listarPorGenero(String genero) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.find(eq("genero", genero)).into(new ArrayList<>()));
    }

    @Override
    public List<Album> buscarPorNombre(String texto) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.find(Filters.regex("nombre", Pattern.quote(texto), "i"))
                        .into(new ArrayList<>()));
    }

    @Override
    public List<Album> buscarPorTexto(String texto) {
        return OperacionesMongo.ejecutar(() -> {
            String patron = Pattern.quote(texto);
            return coleccion.find(Filters.or(
                    Filters.regex("nombre", patron, "i"),
                    Filters.regex("canciones.nombre", patron, "i")))
                    .into(new ArrayList<>());
        });
    }

    @Override
    public boolean existeAlbumDeArtista(ObjectId artistaId, String nombre) {
        return OperacionesMongo.ejecutar(() -> coleccion.countDocuments(
                and(eq("artistaId", artistaId), eq("nombre", nombre)),
                new CountOptions().collation(Colaciones.INSENSIBLE)) > 0);
    }

    @Override
    public long contarPorArtista(ObjectId artistaId) {
        return OperacionesMongo.ejecutar(() -> coleccion.countDocuments(eq("artistaId", artistaId)));
    }

    @Override
    public Album actualizar(Album album) {
        return OperacionesMongo.ejecutar(() -> {
            coleccion.replaceOne(eq("_id", album.getId()), album);
            return album;
        });
    }

    @Override
    public boolean eliminar(ObjectId id) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.deleteOne(eq("_id", id)).getDeletedCount() > 0);
    }

    @Override
    public long eliminarPorArtista(ObjectId artistaId) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.deleteMany(eq("artistaId", artistaId)).getDeletedCount());
    }

    @Override
    public void agregarCancion(ObjectId albumId, Cancion cancion) {
        OperacionesMongo.ejecutar(
                () -> coleccion.updateOne(eq("_id", albumId), Updates.push("canciones", cancion)));
    }

    @Override
    public void eliminarCancion(ObjectId albumId, ObjectId cancionId) {
        OperacionesMongo.ejecutar(() -> coleccion.updateOne(
                eq("_id", albumId),
                Updates.pull("canciones", new Document("_id", cancionId))));
    }
}
