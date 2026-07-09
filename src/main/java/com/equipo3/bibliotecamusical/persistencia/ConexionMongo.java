package com.equipo3.bibliotecamusical.persistencia;

import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.entidades.TipoFavorito;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * Provee la conexion a MongoDB como un unico {@link MongoClient} para toda la
 * vida de la aplicacion (el driver es thread-safe y mantiene un pool interno).
 * Nunca se debe crear un cliente por DAO u operacion.
 *
 * <p>Configura el {@link CodecRegistry} para mapear documentos a POJOs, fechas
 * {@code java.time} (JSR-310) y enums por clave ({@link ClaveEnumCodec}).
 *
 * <p>URI y nombre de BD se pueden sobreescribir con las propiedades de sistema
 * {@code bm.mongo.uri} y {@code bm.mongo.db} (util para pruebas).
 */
public final class ConexionMongo {

    public static final String NOMBRE_BD_POR_DEFECTO = "bibliotecaMusical3";

    private static MongoClient cliente;

    private ConexionMongo() {
    }

    /** @return el cliente unico, creandolo la primera vez. */
    public static synchronized MongoClient getCliente() {
        if (cliente == null) {
            String uri = System.getProperty("bm.mongo.uri", "mongodb://localhost:27017");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .codecRegistry(construirRegistro())
                    .build();
            cliente = MongoClients.create(settings);
            Runtime.getRuntime().addShutdownHook(new Thread(ConexionMongo::cerrar));
        }
        return cliente;
    }

    /** @return la base de datos por defecto ({@code bibliotecaMusical3} o la de {@code bm.mongo.db}). */
    public static MongoDatabase getBaseDatos() {
        String nombre = System.getProperty("bm.mongo.db", NOMBRE_BD_POR_DEFECTO);
        return getCliente().getDatabase(nombre);
    }

    /** @return una base de datos con nombre explicito (usado en pruebas de integracion). */
    public static MongoDatabase getBaseDatos(String nombre) {
        return getCliente().getDatabase(nombre);
    }

    /** @return true si el servidor responde a un ping. */
    public static boolean disponible() {
        try {
            getBaseDatos().runCommand(new Document("ping", 1));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static synchronized void cerrar() {
        if (cliente != null) {
            cliente.close();
            cliente = null;
        }
    }

    private static CodecRegistry construirRegistro() {
        CodecRegistry enums = CodecRegistries.fromCodecs(
                new ClaveEnumCodec<>(TipoArtista.class),
                new ClaveEnumCodec<>(TipoFavorito.class));
        CodecRegistry pojos = CodecRegistries.fromProviders(
                PojoCodecProvider.builder().automatic(true).build());
        // Orden: los codecs de enum ganan sobre el registro por defecto (que trae
        // JSR-310 para fechas); el codec POJO va al final como respaldo general.
        return CodecRegistries.fromRegistries(
                enums,
                MongoClientSettings.getDefaultCodecRegistry(),
                pojos);
    }
}
