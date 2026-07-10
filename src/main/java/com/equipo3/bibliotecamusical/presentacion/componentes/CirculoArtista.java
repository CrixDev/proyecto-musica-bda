package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.dtos.ArtistaDTO;
import com.equipo3.bibliotecamusical.entidades.TipoArtista;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import com.equipo3.bibliotecamusical.presentacion.estilo.Imagenes;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Tarjeta vertical de artista/banda: foto circular + nombre + genero, calcada
 * de las secciones "Artistas" del storyboard. Es clicable para navegar al
 * detalle del artista.
 */
public class CirculoArtista extends JPanel {

    public CirculoArtista(ArtistaDTO artista, int lado, Runnable alHacerClic) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        int ancho = lado + 24;
        setPreferredSize(new Dimension(ancho, lado + 56));
        setMaximumSize(new Dimension(ancho, lado + 56));

        var imagen = Imagenes.cargar(artista.imagen());
        JLabel foto = new JLabel(new ImageIcon(Imagenes.circular(imagen, artista.nombre(), lado)));
        foto.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(foto);
        add(Box.createVerticalStrut(10));

        JLabel nombre = new JLabel(artista.nombre(), SwingConstants.CENTER);
        nombre.setFont(Estilos.TEXTO_NEGRITA);
        nombre.setForeground(Estilos.TEXTO_PRIMARIO);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        nombre.setMaximumSize(new Dimension(ancho, 20));
        nombre.setHorizontalAlignment(SwingConstants.CENTER);
        add(nombre);
        add(Box.createVerticalStrut(2));

        String tipoTexto = artista.tipo() == TipoArtista.BANDA ? "Banda" : "Solista";
        String sub = artista.genero() != null && !artista.genero().isBlank()
                ? artista.genero() : tipoTexto;
        JLabel genero = new JLabel(sub, SwingConstants.CENTER);
        genero.setFont(Estilos.TEXTO_PEQUENO);
        genero.setForeground(Estilos.TEXTO_SECUNDARIO);
        genero.setAlignmentX(Component.CENTER_ALIGNMENT);
        genero.setMaximumSize(new Dimension(ancho, 16));
        genero.setHorizontalAlignment(SwingConstants.CENTER);
        add(genero);

        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        if (alHacerClic != null) {
            MouseAdapter clic = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    alHacerClic.run();
                }
            };
            addMouseListener(clic);
            foto.addMouseListener(clic);
        }
    }
}
