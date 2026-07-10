package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import com.equipo3.bibliotecamusical.presentacion.estilo.Imagenes;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Tarjeta de album/cancion: portada con esquinas redondeadas + titulo + subtitulo
 * (artista). Es una tarjeta con borde suave, clicable, reutilizada en las
 * secciones "Albumes" y "Canciones" del Inicio y en la pantalla de Albumes.
 */
public class TarjetaAlbum extends JPanel {

    private boolean hover = false;

    public TarjetaAlbum(String imagenRef, String titulo, String subtitulo, int ladoPortada, Runnable alHacerClic) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        int ancho = ladoPortada + 24;
        setPreferredSize(new Dimension(ancho, ladoPortada + 70));
        setMaximumSize(new Dimension(ancho, ladoPortada + 70));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        var portadaOrigen = Imagenes.cargar(imagenRef);
        JLabel portada = new JLabel(new ImageIcon(
                Imagenes.redondeada(portadaOrigen, titulo, ladoPortada, ladoPortada, 14)));
        portada.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(portada);
        add(Box.createVerticalStrut(8));

        JLabel lblTitulo = new JLabel(recortar(titulo, 20));
        lblTitulo.setFont(Estilos.TEXTO_NEGRITA);
        lblTitulo.setForeground(Estilos.TEXTO_PRIMARIO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTitulo);

        if (subtitulo != null && !subtitulo.isBlank()) {
            JLabel lblSub = new JLabel(recortar(subtitulo, 24));
            lblSub.setFont(Estilos.TEXTO_PEQUENO);
            lblSub.setForeground(Estilos.TEXTO_SECUNDARIO);
            lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(Box.createVerticalStrut(2));
            add(lblSub);
        }

        MouseAdapter ratón = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (alHacerClic != null) {
                    alHacerClic.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        };
        addMouseListener(ratón);
        portada.addMouseListener(ratón);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hover ? Estilos.SUPERFICIE : Estilos.FONDO);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.setColor(hover ? Estilos.ACENTO_MORADO_CLARO : Estilos.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }

    private static String recortar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + "…";
    }

    /** Color de portada de respaldo, por si se quisiera pintar un bloque plano. */
    public static Color colorPlaceholder() {
        return Estilos.ACENTO_MORADO_CLARO;
    }
}
