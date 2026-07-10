package com.equipo3.bibliotecamusical;

import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.persistencia.ConexionMongo;
import com.equipo3.bibliotecamusical.persistencia.InicializadorBd;
import com.equipo3.bibliotecamusical.presentacion.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion Biblioteca Musical.
 *
 * <p>Arranca el proceso completo: verifica MongoDB, prepara las colecciones,
 * construye los servicios y abre la interfaz grafica empezando por un login
 * <b>simulado</b> (el login real contra la BD queda pendiente). Desde ahi se
 * navega a todas las pantallas ya conectadas: Inicio, Artistas, Albumes,
 * detalle de artista/album, Perfil y Favoritos.
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::iniciar);
    }

    private static void iniciar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, se usa el look and feel por defecto.
        }

        if (!ConexionMongo.disponible()) {
            JOptionPane.showMessageDialog(null,
                    "MongoDB no está disponible en localhost:27017.\n"
                    + "Inicia el servidor de MongoDB (o abre MongoDB Compass y conéctate)\n"
                    + "y vuelve a ejecutar la aplicación.",
                    "Biblioteca Musical", JOptionPane.ERROR_MESSAGE);
            return;
        }

        InicializadorBd.inicializar(ConexionMongo.getBaseDatos());
        Servicios servicios = new Servicios(ConexionMongo.getBaseDatos());

        sembrarSiVacio(servicios);

        VentanaPrincipal.mostrarLoginSimulado(servicios);
    }

    /**
     * Carga los datos de {@code artistas.json} automáticamente al iniciar, pero
     * solo si la base de datos está vacía (es idempotente: en arranques
     * posteriores no vuelve a insertar ni duplica).
     */
    private static void sembrarSiVacio(Servicios servicios) {
        try {
            if (servicios.artistas().contar() == 0) {
                servicios.cargaMasiva().ejecutarCarga();
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudieron cargar los datos iniciales:\n" + e.getMessage(),
                    "Biblioteca Musical", JOptionPane.WARNING_MESSAGE);
        }
    }
}
