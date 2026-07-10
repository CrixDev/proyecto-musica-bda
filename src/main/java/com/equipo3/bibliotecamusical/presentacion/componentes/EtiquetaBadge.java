/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * 
 * @author Dylan
 */
public class EtiquetaBadge extends JLabel {

    public EtiquetaBadge(String texto, Color fondo, Color textoColor) {
        super(texto, SwingConstants.CENTER);
        setOpaque(false);
        setForeground(textoColor);
        setFont(Estilos.BADGE_FUENTE);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12));
        putClientProperty("fondo", fondo);
    }

    public static EtiquetaBadge verde(String texto) {
        return new EtiquetaBadge(texto, Estilos.BADGE_VERDE_FONDO, Estilos.BADGE_VERDE_TEXTO);
    }

    public static EtiquetaBadge morada(String texto) {
        return new EtiquetaBadge(texto, Estilos.BADGE_MORADO_FONDO, Estilos.BADGE_MORADO_TEXTO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor((Color) getClientProperty("fondo"));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
