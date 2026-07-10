package com.equipo3.bibliotecamusical.negocio.servicios;

/**
 * Criterio de ordenamiento de los resultados del buscador.
 */
public enum OrdenBusqueda {

    RELEVANCIA("Relevancia"),
    ALFABETICO_AZ("Nombre (A-Z)"),
    ALFABETICO_ZA("Nombre (Z-A)"),
    ANIO_RECIENTE("Año (más reciente)"),
    ANIO_ANTIGUO("Año (más antiguo)");

    private final String etiqueta;

    OrdenBusqueda(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
