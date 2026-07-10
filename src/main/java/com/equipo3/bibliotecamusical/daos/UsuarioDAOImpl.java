package com.equipo3.bibliotecamusical.daos;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import com.equipo3.bibliotecamusical.entidades.Favorito;
import com.equipo3.bibliotecamusical.entidades.TipoFavorito;
import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;

/** Implementacion MongoDB del repositorio de usuarios. */
public class UsuarioDAOImpl implements IUsuarioDAO {

    private final MongoCollection<Usuario> coleccion;

    public UsuarioDAOImpl(MongoDatabase db) {
        this.coleccion = db.getCollection("usuarios", Usuario.class);
    }

    @Override
    public Usuario crear(Usuario usuario) {
        return OperacionesMongo.ejecutar(() -> {
            InsertOneResult r = coleccion.insertOne(usuario);
            if (usuario.getId() == null && r.getInsertedId() != null) {
                usuario.setId(r.getInsertedId().asObjectId().getValue());
            }
            return usuario;
        });
    }

    @Override
    public Optional<Usuario> buscarPorId(ObjectId id) {
        return OperacionesMongo.ejecutar(
                () -> Optional.ofNullable(coleccion.find(eq("_id", id)).first()));
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return OperacionesMongo.ejecutar(
                () -> Optional.ofNullable(coleccion.find(eq("nombreUsuario", nombreUsuario)).first()));
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return OperacionesMongo.ejecutar(
                () -> Optional.ofNullable(coleccion.find(eq("correo", correo)).first()));
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuarioOCorreo(String valor) {
        return OperacionesMongo.ejecutar(() -> Optional.ofNullable(
                coleccion.find(or(eq("nombreUsuario", valor), eq("correo", valor))).first()));
    }

    @Override
    public boolean existeNombreUsuario(String nombreUsuario) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.countDocuments(eq("nombreUsuario", nombreUsuario)) > 0);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.countDocuments(eq("correo", correo)) > 0);
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        return OperacionesMongo.ejecutar(() -> {
            coleccion.replaceOne(eq("_id", usuario.getId()), usuario);
            return usuario;
        });
    }

    @Override
    public void actualizarContrasena(ObjectId id, String hash) {
        OperacionesMongo.ejecutar(
                () -> coleccion.updateOne(eq("_id", id), Updates.set("contrasena", hash)));
    }

    @Override
    public boolean eliminar(ObjectId id) {
        return OperacionesMongo.ejecutar(
                () -> coleccion.deleteOne(eq("_id", id)).getDeletedCount() > 0);
    }

    @Override
    public void agregarFavorito(ObjectId usuarioId, Favorito favorito) {
        OperacionesMongo.ejecutar(
                () -> coleccion.updateOne(eq("_id", usuarioId), Updates.push("favoritos", favorito)));
    }

    @Override
    public void quitarFavorito(ObjectId usuarioId, TipoFavorito tipo, ObjectId refId) {
        OperacionesMongo.ejecutar(() -> coleccion.updateOne(
                eq("_id", usuarioId),
                Updates.pull("favoritos", new Document("tipo", tipo.getClave()).append("refId", refId))));
    }

    @Override
    public void actualizarGenerosNoDeseados(ObjectId usuarioId, List<String> generos) {
        OperacionesMongo.ejecutar(() -> coleccion.updateOne(
                eq("_id", usuarioId), Updates.set("generosNoDeseados", generos)));
    }
}
