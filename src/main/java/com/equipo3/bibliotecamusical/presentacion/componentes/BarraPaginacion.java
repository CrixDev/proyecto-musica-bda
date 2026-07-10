package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Barra de paginación reutilizable: botones "Anterior/Siguiente" e indicador
 * "Página X de Y". Notifica el índice de página (0-based) al que se quiere ir.
 */
public class BarraPaginacion extends JPanel {

    /**
     * @param paginaActual  índice de página actual (0-based).
     * @param totalPaginas  número total de páginas (&gt;= 1).
     * @param totalItems    número total de elementos (para el texto informativo).
     * @param alIrAPagina   callback con el nuevo índice de página.
     */
    public BarraPaginacion(int paginaActual, int totalPaginas, int totalItems, IntConsumer alIrAPagina) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));

        JButton anterior = boton("‹  Anterior", paginaActual > 0);
        anterior.addActionListener(e -> alIrAPagina.accept(paginaActual - 1));

        JLabel info = new JLabel("Página " + (paginaActual + 1) + " de " + totalPaginas
                + "   ·   " + totalItems + " resultados");
        info.setFont(Estilos.TEXTO_NORMAL);
        info.setForeground(Estilos.TEXTO_SECUNDARIO);

        JButton siguiente = boton("Siguiente  ›", paginaActual < totalPaginas - 1);
        siguiente.addActionListener(e -> alIrAPagina.accept(paginaActual + 1));

        add(anterior);
        add(info);
        add(siguiente);
    }

    private JButton boton(String texto, boolean habilitado) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fondo = !isEnabled() ? Estilos.SUPERFICIE
                        : (getModel().isRollover() ? Estilos.ACENTO_MORADO_CLARO : Estilos.FONDO);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(Estilos.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setEnabled(habilitado);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(habilitado ? Estilos.TEXTO_PRIMARIO : Estilos.TEXTO_SECUNDARIO);
        btn.setFont(Estilos.TEXTO_NEGRITA);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(habilitado ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        return btn;
    }
}
