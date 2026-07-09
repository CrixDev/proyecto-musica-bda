package com.equipo3.bibliotecamusical.persistencia;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;

/** Colaciones reutilizables para comparaciones de texto. */
public final class Colaciones {

    /** Insensible a mayusculas/minusculas (para unicidad y busqueda de nombres). */
    public static final Collation INSENSIBLE = Collation.builder()
            .locale("es")
            .collationStrength(CollationStrength.SECONDARY)
            .build();

    private Colaciones() {
    }
}
