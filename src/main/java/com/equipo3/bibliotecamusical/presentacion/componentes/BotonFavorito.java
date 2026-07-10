/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.function.Consumer;
import javax.swing.JButton;

/**
 * Boton circular con icono de corazon, usado para marcar artistas, albumes y
 * canciones como favoritos.
 *
 * <p>
 * El guardado real en base de datos (coleccion de favoritos del usuario) es
 * responsabilidad del companero que implemente esa fase; este boton expone un
 * {@link #alCambiar(Consumer)} para que ese codigo se conecte sin tocar las
 * vistas de artista/album.
 * 
 * @author Dylan
 */
public class BotonFavorito extends JButton {

    private static final int DIAMETRO = 34;

    private boolean activo;
    private Consumer<Boolean> alCambiar = nuevoEstado -> {
    };

    public BotonFavorito(boolean activoInicial) {
        this.activo = activoInicial;
        setPreferredSize(new Dimension(DIAMETRO, DIAMETRO));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addActionListener(e -> {
            activo = !activo;
            alCambiar.accept(activo);
            repaint();
        });
    }

    /**
     * Registra el callback que se ejecuta cuando el usuario alterna el
     * favorito.
     */
    public void alCambiar(Consumer<Boolean> callback) {
        this.alCambiar = callback != null ? callback : nuevoEstado -> {
        };
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Estilos.FONDO);
        g2.fill(new Ellipse2D.Float(0, 0, DIAMETRO, DIAMETRO));
        g2.setColor(Estilos.BORDE);
        g2.draw(new Ellipse2D.Float(0.5f, 0.5f, DIAMETRO - 1, DIAMETRO - 1));

        GeneralPath corazon = trazoCorazon();
        if (activo) {
            g2.setColor(Estilos.CORAZON_ACTIVO);
            g2.fill(corazon);
        } else {
            g2.setColor(Estilos.CORAZON_INACTIVO);
            g2.setStroke(new java.awt.BasicStroke(1.6f));
            g2.draw(corazon);
        }
        g2.dispose();
    }

    private GeneralPath trazoCorazon() {
        // Corazon dibujado con dos curvas Bezier, centrado en el circulo de 34px.
        GeneralPath p = new GeneralPath();
        float cx = DIAMETRO / 2f;
        float top = 12f;
        p.moveTo(cx, top + 4);
        p.curveTo(cx - 2, top - 2, cx - 9, top - 2, cx - 9, top + 5);
        p.curveTo(cx - 9, top + 11, cx - 3, top + 15, cx, top + 19);
        p.curveTo(cx + 3, top + 15, cx + 9, top + 11, cx + 9, top + 5);
        p.curveTo(cx + 9, top - 2, cx + 2, top - 2, cx, top + 4);
        p.closePath();
        return p;
    }
}
