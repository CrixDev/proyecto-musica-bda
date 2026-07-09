use("bibliotecaMusical3");

// ------------------------------------------------------------
// 1) Insert de un ARTISTA SOLISTA
// ------------------------------------------------------------
db.artistas.insertOne({
    "tipo": "solista",
    "nombre": "Andrés Reyes",
    "imagen": "https://cdn.bibliotecamusical3.com/artistas/andres_reyes.jpg",
    "genero": "Rock": 
   "integrantes": [
	{ nombreCompleto: “Benito sepa” }
   ]
});

// ------------------------------------------------------------
// 2) Insert de un ARTISTA BANDA (con sus integrantes)
// ------------------------------------------------------------
db.artistas.insertOne({
    "tipo": "banda",
    "nombre": "Raíces Callejero",
    "imagen": "https://cdn.bibliotecamusical3.com/artistas/raices_callejero.jpg",
    "genero": "Hip-Hop",
    "integrantes": [
        {
            "nombreCompleto": {
        nombres: "Diego Morales",
       apellidoPaterno: “Sepa”,

},
            "rol": "Baterista",
            "fechaIngreso": "2017-06-20"
        },
        {
            "nombreCompleto": "Héctor López",
            "rol": "Vocalista",
            "fechaIngreso": "2012-06-06"
        },
        {
            "nombreCompleto": "Paula Vázquez",
            "rol": "Percusionista",
            "fechaIngreso": "2019-07-04",
            "fechaSalida": "2024-01-15"        }
    ]
});

// ------------------------------------------------------------
// 3) Insert de un USUARIO con un género no deseado
// ------------------------------------------------------------
db.usuarios.insertOne({
    "nombreUsuario": "usuario1_equipo3",
    "correo": "usuario1_equipo3@bibliotecamusical3.com",
    "contrasena": "12345encriptada",
    "imagenPerfil": "https://cdn.bibliotecamusical3.com/perfiles/usuario1.jpg",
    "generosNoDeseados": ["Metal"]
});
 
// ------------------------------------------------------------
// 4) Agregar un FAVORITO al usuario (usa $push para meterlo al arreglo)
// ------------------------------------------------------------
db.usuarios.updateOne(
    { "nombreUsuario": "usuario1_equipo3" },
    {
        $push: {
            favoritos: {
                "tipo": "artista",
                "nombre": "Andrés Reyes",
                "genero": "Rock",
                "fechaAgregado": "2026-07-08"
            }
        }
    }
);


// ------------------------------------------------------------
// 5) Ver la base de datos completa (estructura)
// ------------------------------------------------------------
db.getCollection("artistas").find({});
db.getCollection("usuarios").find({});

