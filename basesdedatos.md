Base de datos MongoDB Equipo 3

use("bibliotecaMusical3");
 
// ------------------------------------------------------------
// Limpiar colecciones (por si ya existen)
// ------------------------------------------------------------
db.usuarios.drop();
db.artistas.drop();
db.albumes.drop();
 
// ------------------------------------------------------------
// Crear colecciones con validación de esquema ($jsonSchema)
// ------------------------------------------------------------
 
db.createCollection("usuarios", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["nombreUsuario", "correo", "contrasena"],
      properties: {
        nombreUsuario: { bsonType: "string" },
        correo: { bsonType: "string" },
        contrasena: { bsonType: "string" },
        imagenPerfil: { bsonType: ["string", "null"] },
        favoritos: {
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["tipo", "refId", "fechaAgregado"],
            properties: {
              tipo: { enum: ["artista", "album", "cancion"] },
              refId: { bsonType: "objectId" },
              albumId: { bsonType: "objectId" }, // solo si tipo = "cancion"
              genero: { bsonType: "string" },
              fechaAgregado: { bsonType: "string" }
            }
          }
        },
        generosNoDeseados: {
          bsonType: "array",
          items: { bsonType: "string" }
        },
        fechaRegistro: { bsonType: "string" }
      }
    }
  }
});
 
db.createCollection("artistas", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["tipo", "nombre", "genero"],
      properties: {
        tipo: { enum: ["solista", "banda"] },
        nombre: { bsonType: "string" },
        imagen: { bsonType: "string" },
        genero: { bsonType: "string" },
        fechaCreacion: { bsonType: "date" },
        integrantes: { // solo aplica si tipo = "banda"
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["nombreCompleto", "rol", "fechaIngreso", "activo"],
            properties: {
              nombreCompleto: { bsonType: "string" },
              rol: { bsonType: "string" },
              fechaIngreso: { bsonType: "string" },
              fechaSalida: { bsonType: ["string", "null"] },
              activo: { bsonType: "bool" }
            }
          }
        }
      }
    }
  }
});
 
db.createCollection("albumes", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["artistaId", "nombre", "fechaLanzamiento", "genero", "canciones"],
      properties: {
        artistaId: { bsonType: "objectId" },
        nombre: { bsonType: "string" },
        fechaLanzamiento: { bsonType: "string" },
        genero: { bsonType: "string" },
        imagenPortada: { bsonType: "string" },
        canciones: {
          bsonType: "array",
          minItems: 3, // requisito: al menos 3 canciones
          items: {
            bsonType: "object",
            required: ["nombre", "numeroPista", "duracionSegundos"],
            properties: {
              nombre: { bsonType: "string" },
              numeroPista: { bsonType: "int" },
              duracionSegundos: { bsonType: "int" }
            }
          }
        }
      }
    }
  }
});
 
// ------------------------------------------------------------
// Documentos de ejemplo: ARTISTAS
// ------------------------------------------------------------
 
// 1 solista
const idSolista = ObjectId();
db.artistas.insertOne({
  _id: idSolista,
  tipo: "solista",
  nombre: "Andrés Reyes",
  imagen: "https://cdn.bibliotecamusical3.com/artistas/andres_reyes.jpg",
  genero: "Rock",
  fechaCreacion: ISODate("2026-07-07T00:00:00Z")
});
 
// 1 banda (con integrantes activos e inactivos)
const idBanda = ObjectId();
db.artistas.insertOne({
  _id: idBanda,
  tipo: "banda",
  nombre: "Raíces Callejero",
  imagen: "https://cdn.bibliotecamusical3.com/artistas/raices_callejero.jpg",
  genero: "Hip-Hop",
  integrantes: [
    {
      nombreCompleto: "Diego Morales",
      rol: "Baterista",
      fechaIngreso: "2017-06-20",
      fechaSalida: null,
      activo: true
    },
    {
      nombreCompleto: "Héctor López",
      rol: "Vocalista",
      fechaIngreso: "2012-06-06",
      fechaSalida: null,
      activo: true
    },
    {
      nombreCompleto: "Paula Vázquez",
      rol: "Percusionista",
      fechaIngreso: "2019-07-04",
      fechaSalida: "2024-01-15",
      activo: false
    }
  ],
  fechaCreacion: ISODate("2026-07-07T00:00:00Z")
});
 
// ------------------------------------------------------------
// Documentos de ejemplo: ALBUMES (al menos 2 por artista, 3+ canciones c/u)
// ------------------------------------------------------------
 
db.albumes.insertMany([
  {
    _id: ObjectId(),
    artistaId: idSolista,
    nombre: "Distancia",
    fechaLanzamiento: "2018-03-10",
    genero: "Rock",
    imagenPortada: "https://cdn.bibliotecamusical3.com/albumes/distancia.jpg",
    canciones: [
      { _id: ObjectId(), nombre: "Volver", numeroPista: 1, duracionSegundos: 210 },
      { _id: ObjectId(), nombre: "Sin ti", numeroPista: 2, duracionSegundos: 185 },
      { _id: ObjectId(), nombre: "Amanecer", numeroPista: 3, duracionSegundos: 240 }
    ]
  },
  {
    _id: ObjectId(),
    artistaId: idSolista,
    nombre: "Origen",
    fechaLanzamiento: "2021-09-05",
    genero: "Rock",
    imagenPortada: "https://cdn.bibliotecamusical3.com/albumes/origen.jpg",
    canciones: [
      { _id: ObjectId(), nombre: "Camino", numeroPista: 1, duracionSegundos: 198 },
      { _id: ObjectId(), nombre: "Ayer", numeroPista: 2, duracionSegundos: 220 },
      { _id: ObjectId(), nombre: "Huellas", numeroPista: 3, duracionSegundos: 205 }
    ]
  },
  {
    _id: ObjectId(),
    artistaId: idBanda,
    nombre: "Resonancia (Edición especial)",
    fechaLanzamiento: "2011-05-25",
    genero: "Hip-Hop",
    imagenPortada: "https://cdn.bibliotecamusical3.com/albumes/resonancia.jpg",
    canciones: [
      { _id: ObjectId(), nombre: "Reflejo", numeroPista: 1, duracionSegundos: 155 },
      { _id: ObjectId(), nombre: "Otra vez", numeroPista: 2, duracionSegundos: 289 },
      { _id: ObjectId(), nombre: "Eco", numeroPista: 3, duracionSegundos: 255 }
    ]
  },
  {
    _id: ObjectId(),
    artistaId: idBanda,
    nombre: "Renacer",
    fechaLanzamiento: "2016-11-12",
    genero: "Hip-Hop",
    imagenPortada: "https://cdn.bibliotecamusical3.com/albumes/renacer.jpg",
    canciones: [
      { _id: ObjectId(), nombre: "Fuego lento", numeroPista: 1, duracionSegundos: 230 },
      { _id: ObjectId(), nombre: "Piel", numeroPista: 2, duracionSegundos: 190 },
      { _id: ObjectId(), nombre: "Confesión", numeroPista: 3, duracionSegundos: 212 }
    ]
  }
]);
 
// ------------------------------------------------------------
// Documento de ejemplo: USUARIOS
// (contraseña ya hasheada con bcrypt; en la app real se genera con bcrypt.hashSync)
// ------------------------------------------------------------
 
db.usuarios.insertOne({
  _id: ObjectId(),
  nombreUsuario: "usuario1_equipo3",
  correo: "usuario1_equipo3@bibliotecamusical3.com",
  contrasena: "$2b$08$nvjpdO/HJi3MvRtU6ceO/uHbudEheCzHJdnFvT2tTRkcR1YXgG2za",
  imagenPerfil: "https://cdn.bibliotecamusical3.com/perfiles/usuario1.jpg",
  favoritos: [
    { tipo: "artista", refId: idSolista, genero: "Rock", fechaAgregado: "2026-03-30" },
    { tipo: "album", refId: idBanda, genero: "Hip-Hop", fechaAgregado: "2025-03-30" }
  ],
  generosNoDeseados: ["Metal", "Punk"],
  fechaRegistro: "2025-06-03"
});
 
// ------------------------------------------------------------
// Índices
// ------------------------------------------------------------
 
db.artistas.createIndex({ nombre: "text" });
db.artistas.createIndex({ genero: 1 });
db.artistas.createIndex({ tipo: 1 });
 
db.albumes.createIndex({ nombre: "text" });
db.albumes.createIndex({ genero: 1 });
db.albumes.createIndex({ fechaLanzamiento: 1 });
db.albumes.createIndex({ artistaId: 1 });
 
db.usuarios.createIndex({ nombreUsuario: 1 }, { unique: true });
db.usuarios.createIndex({ correo: 1 }, { unique: true });
 
print("Colecciones creadas: usuarios, artistas, albumes");
print("Artistas: " + db.artistas.countDocuments());
print("Albumes: " + db.albumes.countDocuments());
print("Usuarios: " + db.usuarios.countDocuments());
