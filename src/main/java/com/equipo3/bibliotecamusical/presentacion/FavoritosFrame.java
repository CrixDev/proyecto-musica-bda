/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion;

/**
 *
 * @author alecn
 */
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de "Favoritos" de Biblioteca Musical: barra lateral de
 * navegacion + contenido con artistas/albumes favoritos y canciones
 * favoritas. Solo interfaz visual (sin logica de negocio real).
 */
public class FavoritosFrame {

    private static final Color MORADO = new Color(0x7C3AED);
    private static final Color MORADO_CLARO = new Color(0xF1E9FE);
    private static final Color GRIS_TEXTO = new Color(0x8A8A93);
    private static final Color GRIS_BORDE = new Color(0xE5E5EA);
    private static final Color TEXTO_OSCURO = new Color(0x1A1A2E);
    private static final Color ROSA_TEXTO = new Color(0xD6336C);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> crearFavoritos().setVisible(true));
    }

    // =========================================================
    //  VENTANA PRINCIPAL
    // =========================================================
    public static JFrame crearFavoritos() {
        JFrame frame = new JFrame("Biblioteca Musical - Favoritos");
        frame.setSize(1100, 650);
        frame.setMinimumSize(new Dimension(900, 550));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(crearSidebar(frame), BorderLayout.WEST);
        frame.add(crearContenido(frame), BorderLayout.CENTER);

        return frame;
    }

    // =========================================================
    //  BARRA LATERAL
    // =========================================================
    static JPanel crearSidebar(JFrame parentFrame) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, 650));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, GRIS_BORDE));

        JPanel top = new JPanel(null);
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(230, 560));

        // Logo
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x36D1DC), getWidth(), getHeight(), new Color(0x7C3AED));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                FontMetrics fm = g2.getFontMetrics();
                String s = "\u266A";
                g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, getHeight() / 2 + 6);
                g2.dispose();
            }
        };
        logoIcon.setOpaque(false);
        logoIcon.setBounds(20, 18, 34, 34);
        top.add(logoIcon);

        JLabel logoTxt = new JLabel("<html>Biblioteca<br>Musical</html>");
        logoTxt.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoTxt.setForeground(TEXTO_OSCURO);
        logoTxt.setBounds(62, 15, 150, 40);
        top.add(logoTxt);

        int y = 80;
        top.add(crearNavItem("\u2302", "Inicio", false, 20, y, parentFrame));
        y += 42;
        top.add(crearNavItem("\uD83D\uDD0D", "Buscar", false, 20, y, parentFrame));
        y += 42;
        top.add(crearNavItem("\uD83C\uDFA4", "Artistas", false, 20, y, parentFrame));
        y += 42;
        top.add(crearNavItem("\uD83D\uDCBF", "\u00C1lbumes", false, 20, y, parentFrame));

        y += 50;
        JLabel seccion = new JLabel("TU BIBLIOTECA");
        seccion.setFont(new Font("SansSerif", Font.BOLD, 10));
        seccion.setForeground(GRIS_TEXTO);
        seccion.setBounds(22, y, 200, 16);
        top.add(seccion);

        y += 26;
        top.add(crearNavItem("\u2665", "Favoritos", true, 20, y, parentFrame));
        y += 42;
        top.add(crearNavItem("\uD83D\uDC64", "Perfil", false, 20, y, parentFrame));

        sidebar.add(top, BorderLayout.CENTER);

        // ---- Panel de Usuario Inferior ----
        JPanel bottom = new JPanel(null);
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(230, 70));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE));

        String nombreUsuarioLogueado = SesionActual.hayUsuario() ? SesionActual.getUsuario().getNombreUsuario() : "Cristian Devora";
        String tagUsuarioLogueado = "@" + nombreUsuarioLogueado.toLowerCase().replace(" ", "_");
        String iniciales = nombreUsuarioLogueado.substring(0, Math.min(2, nombreUsuarioLogueado.length())).toUpperCase();

        JPanel avatar = crearAvatarCirculo(36, iniciales);
        avatar.setBounds(20, 17, 36, 36);
        bottom.add(avatar);

        JLabel nombreLabel = new JLabel(nombreUsuarioLogueado);
        nombreLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nombreLabel.setForeground(TEXTO_OSCURO);
        nombreLabel.setBounds(66, 14, 150, 16);
        bottom.add(nombreLabel);

        JLabel usuarioLabel = new JLabel(tagUsuarioLogueado);
        usuarioLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        usuarioLabel.setForeground(GRIS_TEXTO);
        usuarioLabel.setBounds(66, 31, 150, 16);
        bottom.add(usuarioLabel);

        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    static JPanel crearNavItem(String icono, String texto, boolean activo, int x, int y, JFrame parentFrame) {
        JPanel item = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                if (activo) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(MORADO_CLARO);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setBounds(x, y, 190, 34);

        JLabel ic = new JLabel(icono);
        ic.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ic.setForeground(activo ? MORADO : GRIS_TEXTO);
        ic.setBounds(10, 7, 24, 20);
        item.add(ic);

        JLabel txt = new JLabel(texto);
        txt.setFont(new Font("SansSerif", activo ? Font.BOLD : Font.PLAIN, 12));
        txt.setForeground(activo ? MORADO : new Color(0x444444));
        txt.setBounds(38, 7, 140, 20);
        item.add(txt);

        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Enrutamiento funcional entre vistas de la barra lateral
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!activo) {
                    if (texto.equals("Perfil")) {
                        parentFrame.dispose();
                        PerfilFrame.crearPerfil().setVisible(true);
                    }
                    // Aquí puedes ir enlazando los frames de Inicio, Buscar, etc.
                }
            }
        });

        return item;
    }

    // =========================================================
    //  CONTENIDO PRINCIPAL
    // =========================================================
    static JPanel crearContenido(JFrame parentFrame) {
        JPanel content = new JPanel(null);
        content.setBackground(Color.WHITE);

        String nombreReal = SesionActual.hayUsuario() ? SesionActual.getUsuario().getNombreUsuario() : "Cristian Devora";
        String inicialesGrandes = nombreReal.substring(0, Math.min(2, nombreReal.length())).toUpperCase();

        // ---- Barra superior ----
        JLabel back = new JLabel("\u2039");
        back.setFont(new Font("SansSerif", Font.BOLD, 18));
        back.setForeground(GRIS_TEXTO);
        back.setBounds(30, 22, 20, 24);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        content.add(back);

        JLabel breadcrumb = new JLabel("Favoritos");
        breadcrumb.setFont(new Font("SansSerif", Font.BOLD, 14));
        breadcrumb.setForeground(TEXTO_OSCURO);
        breadcrumb.setBounds(55, 22, 150, 24);
        content.add(breadcrumb);

        JPanel userMini = crearAvatarCirculo(28, inicialesGrandes);
        content.add(userMini);

        JLabel userMiniName = new JLabel(nombreReal.split(" ")[0]);
        userMiniName.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userMiniName.setForeground(TEXTO_OSCURO);
        content.add(userMiniName);

        JLabel chevron = new JLabel("\u2304");
        chevron.setFont(new Font("SansSerif", Font.BOLD, 12));
        chevron.setForeground(GRIS_TEXTO);
        chevron.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        content.add(chevron);

        // Desplegable de Cierre de Sesión en Flecha Superior
        JPopupMenu logoutMenu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("Cerrar sesión \u21AA");
        logoutItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutItem.setForeground(ROSA_TEXTO);
        logoutItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutItem.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(parentFrame, "¿Cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                SesionActual.cerrar();
                parentFrame.dispose();
            }
        });
        logoutMenu.add(logoutItem);
        chevron.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logoutMenu.show(chevron, -80, chevron.getHeight() + 4);
            }
        });

        // ---- Sección: Artistas y álbumes ----
        JLabel tituloArtistas = new JLabel("Artistas y \u00e1lbumes");
        tituloArtistas.setFont(new Font("SansSerif", Font.BOLD, 15));
        tituloArtistas.setForeground(TEXTO_OSCURO);
        tituloArtistas.setBounds(30, 75, 300, 22);
        content.add(tituloArtistas);

        // Datos quemados para renderizar portadas con colores calculados por Hash matemático
        String[][] artistas = {
            {"Guns N' Roses", "Artista"},
            {"Blurryface", "Twenty One Pilots"},
            {"OK Computer", "Radiohead"},
            {"Michael Jackson", "Artista"},
            {"AM", "Arctic Monkeys"},
            {"Trench", "Twenty One Pilots"}
        };

        int x = 30;
        for (String[] a : artistas) {
            // Genera colores estables basados en el nombre del elemento para simular una portada real
            int hash = a[0].hashCode();
            Color c1 = new Color((hash & 0xFF0000) >> 16, (hash & 0x00FF00) >> 8, hash & 0x0000FF);
            Color c2 = c1.brighter();
            
            JPanel card = crearTarjetaAlbum(a[0], a[1], c1, c2);
            card.setBounds(x, 105, 95, 128);
            content.add(card);
            x += 108;
        }

        // ---- Línea divisoria ----
        JPanel linea = new JPanel();
        linea.setBackground(GRIS_BORDE);
        linea.setBounds(30, 250, 1000, 1);
        content.add(linea);

        // ---- Sección: Canciones ----
        JLabel tituloCanciones = new JLabel("Canciones");
        tituloCanciones.setFont(new Font("SansSerif", Font.BOLD, 15));
        tituloCanciones.setForeground(TEXTO_OSCURO);
        tituloCanciones.setBounds(30, 270, 300, 22);
        content.add(tituloCanciones);

        // Contenedor dinámico vertical para las filas de canciones
        JPanel panelListaCanciones = new JPanel(null);
        panelListaCanciones.setOpaque(false);
        panelListaCanciones.setBounds(30, 305, 1000, 250);
        content.add(panelListaCanciones);

        // Renderizado funcional del listado de canciones favoritas
        String[][] cancionesData = {
            {"Car Radio", "Blurryface", "Twenty One Pilots", "4:35"},
            {"R U Mine?", "AM", "Arctic Monkeys", "3:58"}
        };

        int yFila = 0;
        for (String[] c : cancionesData) {
            int hash = c[0].hashCode();
            Color c1 = new Color((hash & 0xFF0000) >> 16, (hash & 0x00FF00) >> 8, hash & 0x0000FF);
            
            JPanel fila = crearFilaCancion(c[0], c[1], c[2], c[3], c1, c1.darker(), panelListaCanciones);
            fila.setBounds(0, yFila, 1000, 50);
            panelListaCanciones.add(fila);
            yFila += 55;
        }

        // Distribución responsiva en redimensionamientos de ventana
        content.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = content.getWidth();
                userMini.setBounds(w - 150, 20, 28, 28);
                userMiniName.setBounds(w - 115, 24, 75, 20);
                chevron.setBounds(w - 35, 24, 16, 20);
                linea.setSize(w - 60, 1);
                panelListaCanciones.setSize(w - 60, 250);
                for (Component comp : panelListaCanciones.getComponents()) {
                    comp.setSize(w - 60, 50);
                    if (comp instanceof JPanel) {
                        Component[] subComps = ((JPanel) comp).getComponents();
                        for (Component sub : subComps) {
                            if (sub instanceof JLabel && ((JLabel) sub).getText().equals("\u2665")) {
                                sub.setLocation(w - 110, 10); // Recoloca el corazón al extremo derecho
                            }
                            if (sub instanceof JLabel && ((JLabel) sub).getHorizontalAlignment() == SwingConstants.RIGHT) {
                                sub.setLocation(w - 190, 12); // Recoloca la duración proporcionalmente
                            }
                        }
                    }
                }
            }
        });

        return content;
    }

    static JPanel crearTarjetaAlbum(String titulo, String subtitulo, Color c1, Color c2) {
        JPanel card = new JPanel(null);
        card.setOpaque(false);

        JPanel portada = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255, 255, 255, 160));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 26));
                FontMetrics fm = g2.getFontMetrics();
                String s = "\u266B";
                g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, getHeight() / 2 + 10);
                g2.dispose();
            }
        };
        portada.setOpaque(false);
        portada.setBounds(0, 0, 95, 95);
        card.add(portada);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitulo.setForeground(TEXTO_OSCURO);
        lblTitulo.setBounds(0, 100, 95, 16);
        card.add(lblTitulo);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSub.setForeground(GRIS_TEXTO);
        lblSub.setBounds(0, 116, 95, 14);
        card.add(lblSub);

        return card;
    }

    static JPanel crearFilaCancion(String titulo, String subtitulo, String artista, String duracion,
                                   Color c1, Color c2, JPanel contenedorPadre) {
        JPanel fila = new JPanel(null);
        fila.setOpaque(false);

        JPanel portada = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        portada.setOpaque(false);
        portada.setBounds(0, 0, 42, 42);
        fila.add(portada);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitulo.setForeground(TEXTO_OSCURO);
        lblTitulo.setBounds(54, 3, 220, 18);
        fila.add(lblTitulo);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSub.setForeground(GRIS_TEXTO);
        lblSub.setBounds(54, 21, 220, 16);
        fila.add(lblSub);

        JLabel lblArtista = new JLabel(artista);
        lblArtista.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblArtista.setForeground(new Color(0x444444));
        lblArtista.setBounds(450, 12, 200, 18);
        fila.add(lblArtista);

        JLabel lblDuracion = new JLabel(duracion);
        lblDuracion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDuracion.setForeground(GRIS_TEXTO);
        lblDuracion.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDuracion.setBounds(760, 12, 60, 18);
        fila.add(lblDuracion);

        // Corazón funcional para remover de favoritos de forma dinámica e inmediata
        JLabel corazon = new JLabel("\u2665");
        corazon.setFont(new Font("SansSerif", Font.BOLD, 16));
        corazon.setForeground(MORADO);
        corazon.setBounds(840, 10, 24, 22);
        corazon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        corazon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(
                    contenedorPadre, 
                    "¿Deseas eliminar \"" + titulo + "\" de tus canciones favoritas?", 
                    "Quitar Favorito", 
                    JOptionPane.YES_NO_OPTION
                );
                if (confirmacion == JOptionPane.YES_OPTION) {
                    // Remueve visualmente la fila del contenedor de inmediato
                    contenedorPadre.remove(fila);
                    
                    // Reacomoda verticalmente las canciones restantes
                    int nuevaY = 0;
                    for (Component c : contenedorPadre.getComponents()) {
                        c.setLocation(c.getX(), nuevaY);
                        nuevaY += 55;
                    }
                    contenedorPadre.revalidate();
                    contenedorPadre.repaint();
                }
            }
        });
        fila.add(corazon);

        return fila;
    }

    static JPanel crearAvatarCirculo(int diametro, String iniciales) {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0xF6A6B2), getWidth(), getHeight(), new Color(0x9575CD));
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, diametro / 3)));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(iniciales, (getWidth() - fm.stringWidth(iniciales)) / 2, getHeight() / 2 + fm.getAscent() / 3);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(diametro, diametro));
        return avatar;
    }
}
