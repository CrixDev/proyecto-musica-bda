/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * 
 * @author Dylan
 */
public class BotonPildora extends JButton {

    private final boolean rellena;
    private Color colorBase;
    private Color colorHover;

    public BotonPildora(String texto, boolean rellena) {
        super(texto);
        this.rellena = rellena;
        this.colorBase = rellena ? Estilos.ACENTO_TEAL : Estilos.FONDO;
        this.colorHover = rellena ? Estilos.ACENTO_TEAL_OSCURO : Estilos.SUPERFICIE;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(rellena ? Estilos.TEXTO_PRIMARIO : Estilos.TEXTO_PRIMARIO);
        setFont(Estilos.TEXTO_NEGRITA);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 22, 10, 24));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(colorHover);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fondo = getModel().isRollover() ? colorHover : colorBase;
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        if (!rellena) {
            g2.setColor(Estilos.BORDE);
            g2.setStroke(new java.awt.BasicStroke(1.4f));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, getHeight(), getHeight());
        }
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean contains(int x, int y) {
        // Hit-test elipsoidal para que el cursor de mano solo se active dentro de la pildora.
        return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight())
                .contains(x, y);
    }
}
