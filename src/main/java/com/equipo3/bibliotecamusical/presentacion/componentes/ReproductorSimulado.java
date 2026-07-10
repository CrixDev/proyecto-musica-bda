package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import com.equipo3.bibliotecamusical.presentacion.estilo.Formato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * Reproductor <b>simulado</b>: no reproduce audio, solo simula el avance de una
 * canción según su duración (una barra que avanza 1 segundo por segundo real),
 * con botón de play/pausa. Se muestra como una barra inferior al reproducir.
 */
public class ReproductorSimulado extends JPanel {

    private final JLabel lblTitulo = new JLabel();
    private final JLabel lblActual = new JLabel("0:00");
    private final JLabel lblTotal = new JLabel("0:00");
    private final JProgressBar barra = new JProgressBar();
    private final JButton btnPlay = new JButton("▶");

    private Timer timer;
    private int segundos;
    private int total;
    private boolean reproduciendo;

    public ReproductorSimulado() {
        setLayout(new BorderLayout(14, 0));
        setBackground(Estilos.SUPERFICIE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.BORDE),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)));
        setVisible(false); // oculto hasta que se reproduzca algo

        btnPlay.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btnPlay.setForeground(Estilos.ACENTO_TEAL_OSCURO);
        btnPlay.setContentAreaFilled(false);
        btnPlay.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        btnPlay.setFocusPainted(false);
        btnPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPlay.addActionListener(e -> alternarPausa());
        add(btnPlay, BorderLayout.WEST);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        lblTitulo.setFont(Estilos.TEXTO_NEGRITA);
        lblTitulo.setForeground(Estilos.TEXTO_PRIMARIO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(lblTitulo);
        centro.add(Box.createVerticalStrut(6));

        barra.setMinimum(0);
        barra.setValue(0);
        barra.setStringPainted(false);
        barra.setForeground(Estilos.ACENTO_TEAL);
        barra.setBackground(Estilos.BORDE);
        barra.setBorderPainted(false);
        barra.setPreferredSize(new Dimension(10, 6));

        lblActual.setFont(Estilos.TEXTO_PEQUENO);
        lblActual.setForeground(Estilos.TEXTO_SECUNDARIO);
        lblTotal.setFont(Estilos.TEXTO_PEQUENO);
        lblTotal.setForeground(Estilos.TEXTO_SECUNDARIO);

        JPanel filaProgreso = new JPanel(new BorderLayout(10, 0));
        filaProgreso.setOpaque(false);
        filaProgreso.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaProgreso.add(lblActual, BorderLayout.WEST);
        filaProgreso.add(barra, BorderLayout.CENTER);
        filaProgreso.add(lblTotal, BorderLayout.EAST);
        centro.add(filaProgreso);

        add(centro, BorderLayout.CENTER);

        JButton cerrar = new JButton("✕");
        cerrar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cerrar.setForeground(Estilos.TEXTO_SECUNDARIO);
        cerrar.setContentAreaFilled(false);
        cerrar.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        cerrar.setFocusPainted(false);
        cerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cerrar.setToolTipText("Detener");
        cerrar.addActionListener(e -> detener());
        add(cerrar, BorderLayout.EAST);
    }

    /** Inicia la reproducción simulada de una canción según su duración. */
    public void reproducir(String titulo, String subtitulo, int duracionSegundos) {
        detenerTimer();
        this.total = Math.max(1, duracionSegundos);
        this.segundos = 0;
        this.reproduciendo = true;

        String texto = titulo == null ? "" : titulo;
        if (subtitulo != null && !subtitulo.isBlank()) {
            texto += "  ·  " + subtitulo;
        }
        lblTitulo.setText("♪  " + texto);
        barra.setMaximum(total);
        barra.setValue(0);
        lblActual.setText(Formato.duracion(0));
        lblTotal.setText(Formato.duracion(total));
        btnPlay.setText("⏸");

        setVisible(true);
        revalidate();
        repaint();

        timer = new Timer(1000, e -> tick());
        timer.start();
    }

    private void tick() {
        segundos++;
        if (segundos >= total) {
            segundos = total;
            actualizar();
            detenerTimer();
            reproduciendo = false;
            btnPlay.setText("▶"); // terminó
            return;
        }
        actualizar();
    }

    private void actualizar() {
        barra.setValue(segundos);
        lblActual.setText(Formato.duracion(segundos));
    }

    private void alternarPausa() {
        if (timer == null) {
            // Si ya terminó, reinicia la misma canción desde el principio.
            if (total > 0) {
                segundos = 0;
                reproduciendo = true;
                btnPlay.setText("⏸");
                timer = new Timer(1000, e -> tick());
                timer.start();
            }
            return;
        }
        if (reproduciendo) {
            timer.stop();
            reproduciendo = false;
            btnPlay.setText("▶");
        } else {
            timer.start();
            reproduciendo = true;
            btnPlay.setText("⏸");
        }
    }

    /** Detiene la reproducción y oculta la barra. */
    public void detener() {
        detenerTimer();
        reproduciendo = false;
        setVisible(false);
        revalidate();
        repaint();
    }

    private void detenerTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    public int getSegundos() {
        return segundos;
    }

    public int getTotal() {
        return total;
    }

    public boolean estaReproduciendo() {
        return reproduciendo;
    }

    /** Color de acento (por si se quiere reutilizar). */
    public static Color acento() {
        return Estilos.ACENTO_TEAL;
    }
}
