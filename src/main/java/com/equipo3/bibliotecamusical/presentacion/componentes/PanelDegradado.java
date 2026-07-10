/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * 
 * @author Dylan
 */
public class PanelDegradado extends JPanel {

    private Color colorInicio = Estilos.BANNER_INICIO;
    private Color colorFin = Estilos.BANNER_FIN;

    public PanelDegradado() {
        setOpaque(false);
    }

    public void setColores(Color inicio, Color fin) {
        this.colorInicio = inicio;
        this.colorFin = fin;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, colorInicio, 0, getHeight(), colorFin));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
