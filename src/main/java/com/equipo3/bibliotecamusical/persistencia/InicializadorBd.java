package com.equipo3.bibliotecamusical.persistencia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;

/**
 * Crea (si no existen) las colecciones {@code usuarios}, {@code artistas} y
 * {@code albumes} con sus validadores {@code $jsonSchema} e indices. Es
 * idempotente: puede llamarse en cada arranque de la app o antes de las pruebas.
 *
 * <p>El esquema aplica las reconciliaciones acordadas: fechas como BSON
 * {@code date}, canciones con {@code _id} y {@code genero}, e indices unicos para
 * evitar duplicados (con collation insensible a mayusculas).
 */
public final class InicializadorBd {

    private InicializadorBd() {
    }

    public static void inicializar(MongoDatabase db) {
        List<String> existentes = new ArrayList<>();
        db.listCollectionNames().into(existentes);

        if (!existentes.contains("usuarios")) {
            crearConValidador(db, "usuarios", esquemaUsuarios());
        }
        if (!existentes.contains("artistas")) {
            crearConValidador(db, "artistas", esquemaArtistas());
        }
        if (!existentes.contains("albumes")) {
            crearConValidador(db, "albumes", esquemaAlbumes());
        }
        crearIndices(db);
    }

    private static void crearConValidador(MongoDatabase db, String nombre, Document jsonSchema) {
        ValidationOptions vo = new ValidationOptions().validator(new Document("$jsonSchema", jsonSchema));
        db.createCollection(nombre, new CreateCollectionOptions().validationOptions(vo));
    }

    private static void crearIndices(MongoDatabase db) {
        MongoCollection<Document> usuarios = db.getCollection("usuarios");
        usuarios.createIndex(Indexes.ascending("nombreUsuario"), new IndexOptions().unique(true));
        usuarios.createIndex(Indexes.ascending("correo"), new IndexOptions().unique(true));

        MongoCollection<Document> artistas = db.getCollection("artistas");
        artistas.createIndex(Indexes.ascending("nombre", "tipo"),
                new IndexOptions().unique(true).collation(Colaciones.INSENSIBLE).name("uq_artista_nombre_tipo"));
        artistas.createIndex(Indexes.ascending("genero"));
        artistas.createIndex(Indexes.ascending("tipo"));

        MongoCollection<Document> albumes = db.getCollection("albumes");
        albumes.createIndex(Indexes.ascending("artistaId", "nombre"),
                new IndexOptions().unique(true).collation(Colaciones.INSENSIBLE).name("uq_album_artista_nombre"));
        albumes.createIndex(Indexes.ascending("genero"));
        albumes.createIndex(Indexes.ascending("fechaLanzamiento"));
        albumes.createIndex(Indexes.ascending("artistaId"));
    }

    // ------------------------------------------------------------------
    // Esquemas $jsonSchema
    // ------------------------------------------------------------------

    private static Document esquemaUsuarios() {
        Document favorito = objeto(
                Arrays.asList("tipo", "refId", "genero", "fechaAgregado"),
                new Document()
                        .append("tipo", new Document("enum", Arrays.asList("artista", "album", "cancion")))
                        .append("refId", tipo("objectId"))
                        .append("albumId", tipo("objectId"))
                        .append("genero", tipo("string"))
                        .append("fechaAgregado", tipo("date")));

        return objeto(
                Arrays.asList("nombreUsuario", "correo", "contrasena", "fechaRegistro"),
                new Document()
                        .append("nombreUsuario", tipo("string"))
                        .append("correo", tipo("string"))
                        .append("contrasena", tipo("string"))
                        .append("imagenPerfil", tipoNullable("string"))
                        .append("favoritos", arregloDe(favorito, 0))
                        .append("generosNoDeseados", arregloDe(tipo("string"), 0))
                        .append("fechaRegistro", tipo("date")));
    }

    private static Document esquemaArtistas() {
        Document integrante = objeto(
                Arrays.asList("nombreCompleto", "rol", "fechaIngreso", "activo"),
                new Document()
                        .append("nombreCompleto", tipo("string"))
                        .append("rol", tipo("string"))
                        .append("fechaIngreso", tipo("date"))
                        .append("fechaSalida", tipoNullable("date"))
                        .append("activo", tipo("bool")));

        return objeto(
                Arrays.asList("tipo", "nombre", "genero", "fechaCreacion"),
                new Document()
                        .append("tipo", new Document("enum", Arrays.asList("solista", "banda")))
                        .append("nombre", tipo("string"))
                        .append("imagen", tipoNullable("string"))
                        .append("genero", tipo("string"))
                        .append("fechaCreacion", tipo("date"))
                        .append("integrantes", arregloDe(integrante, 0)));
    }

    private static Document esquemaAlbumes() {
        Document cancion = objeto(
                Arrays.asList("_id", "nombre", "numeroPista", "duracionSegundos", "genero"),
                new Document()
                        .append("_id", tipo("objectId"))
                        .append("nombre", tipo("string"))
                        .append("numeroPista", tipo("int"))
                        .append("duracionSegundos", tipo("int"))
                        .append("genero", tipo("string")));

        return objeto(
                Arrays.asList("artistaId", "nombre", "fechaLanzamiento", "genero", "canciones"),
                new Document()
                        .append("artistaId", tipo("objectId"))
                        .append("nombre", tipo("string"))
                        .append("fechaLanzamiento", tipo("date"))
                        .append("genero", tipo("string"))
                        .append("imagenPortada", tipoNullable("string"))
                        .append("canciones", arregloDe(cancion, 3)));
    }

    // ------------------------------------------------------------------
    // Ayudas para construir el esquema
    // ------------------------------------------------------------------

    private static Document tipo(String bsonType) {
        return new Document("bsonType", bsonType);
    }

    private static Document tipoNullable(String bsonType) {
        return new Document("bsonType", Arrays.asList(bsonType, "null"));
    }

    private static Document objeto(List<String> requeridos, Document propiedades) {
        return new Document("bsonType", "object")
                .append("required", requeridos)
                .append("properties", propiedades);
    }

    private static Document arregloDe(Document itemSchema, int minItems) {
        Document arr = new Document("bsonType", "array").append("items", itemSchema);
        if (minItems > 0) {
            arr.append("minItems", minItems);
        }
        return arr;
    }
}
