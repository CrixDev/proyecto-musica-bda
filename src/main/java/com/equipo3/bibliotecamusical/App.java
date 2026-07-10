package com.equipo3.bibliotecamusical;

/**
 * Punto de entrada de la aplicacion Biblioteca Musical.
 *
 * <p>En el alcance actual (Fases 1-6, CRUD backend sin interfaz grafica) este
 * main solo informa como ejercitar el CRUD. La interfaz Swing se conecta aqui
 * en la Fase 7. Para probar el CRUD extremo a extremo se usa
 * {@link com.equipo3.bibliotecamusical.presentacion.RunnerCrud}.
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {    
        System.out.println("Biblioteca Musical - Equipo 3");
        System.out.println("Backend CRUD (Fases 1-6). La interfaz grafica llega en la Fase 7.");
        System.out.println("Para una demostracion del CRUD ejecuta la clase RunnerCrud:");
        System.out.println("  mvn -q compile exec:java "
                + "-Dexec.mainClass=com.equipo3.bibliotecamusical.presentacion.RunnerCrud");
    }
}
