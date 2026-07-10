/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion;


import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de "Perfil" de Biblioteca Musical: barra lateral de navegacion +
 * contenido con datos del usuario, tarjetas de estadisticas y generos no
 * deseados. Solo interfaz visual (sin logica de negocio real).
 */
public class PerfilFrame {

    private static final Color MORADO = new Color(0x7C3AED);
    private static final Color MORADO_CLARO = new Color(0xF1E9FE);
    private static final Color GRIS_TEXTO = new Color(0x8A8A93);
    private static final Color GRIS_BORDE = new Color(0xE5E5EA);
    private static final Color TEXTO_OSCURO = new Color(0x1A1A2E);
    private static final Color ROSA_BG = new Color(0xFCE4EC);
    private static final Color ROSA_TEXTO = new Color(0xD6336C);
    
    private static JPanel statsPanelRef;
    
    // Referencias a las etiquetas de las tarjetas para actualizarlas dinámicamente
    private static JLabel lblNumFavoritos;
    private static JLabel lblNumArtistas;
    private static JLabel lblNumRestringidos;

    public static void main(String[] args) {
        // Simulación de sesión por si lo corres directo desde aquí (puedes borrar estas 3 líneas si corres desde el login)
        if (!SesionActual.hayUsuario()) {
            // Asumiendo que tu entidad Usuario tiene un constructor por defecto o setters
            // com.equipo3.bibliotecamusical.entidades.Usuario u = new ...
        }
        SwingUtilities.invokeLater(() -> crearPerfil().setVisible(true));
    }

    // =========================================================
    //  VENTANA PRINCIPAL
    // =========================================================
    public static JFrame crearPerfil() {
        JFrame frame = new JFrame("Biblioteca Musical - Perfil");
        frame.setSize(1100, 650);
        frame.setMinimumSize(new Dimension(900, 550));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(crearSidebar(), BorderLayout.WEST);
        frame.add(crearContenido(frame), BorderLayout.CENTER);

        return frame;
    }

    // =========================================================
    //  BARRA LATERAL
    // =========================================================
    static JPanel crearSidebar() {
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
        top.add(crearNavItem("\u2302", "Inicio", false, 20, y));
        y += 42;
        top.add(crearNavItem("\uD83D\uDD0D", "Buscar", false, 20, y));
        y += 42;
        top.add(crearNavItem("\uD83C\uDFA4", "Artistas", false, 20, y));
        y += 42;
        top.add(crearNavItem("\uD83D\uDCBF", "\u00C1lbumes", false, 20, y));

        y += 50;
        JLabel seccion = new JLabel("TU BIBLIOTECA");
        seccion.setFont(new Font("SansSerif", Font.BOLD, 10));
        seccion.setForeground(GRIS_TEXTO);
        seccion.setBounds(22, y, 200, 16);
        top.add(seccion);

        y += 26;
        top.add(crearNavItem("\u2661", "Favoritos", false, 20, y));
        y += 42;
        top.add(crearNavItem("\uD83D\uDC64", "Perfil", true, 20, y));

        sidebar.add(top, BorderLayout.CENTER);

        // ---- Parte inferior: Datos estables de barra lateral ----
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

    static JPanel crearNavItem(String icono, String texto, boolean activo, int x, int y) {
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
        return item;
    }

    // =========================================================
    //  CONTENIDO PRINCIPAL
    // =========================================================
    static JPanel crearContenido(JFrame parentFrame) {
        JPanel content = new JPanel(null);
        content.setBackground(Color.WHITE);

        // Extraer datos reales del Usuario en sesión
        String nombreReal = SesionActual.hayUsuario() ? SesionActual.getUsuario().getNombreUsuario() : "Cristian Devora";
        String correoReal = SesionActual.hayUsuario() ? SesionActual.getUsuario().getCorreo() : "cristiandevora@gmail.com";
        String tagReal = "@" + nombreReal.toLowerCase().replace(" ", "_");
        String inicialesGrandes = nombreReal.substring(0, Math.min(2, nombreReal.length())).toUpperCase();

        // Obtener contadores reales directamente desde las colecciones/listas de la base de datos mapeadas en tu Usuario
        // Si las listas están inicializadas como null, nos aseguramos de pintar 0
        int favsCount = 0;
        int artistasCount = 0;
        int restringidosIniciales = 0;

        if (SesionActual.hayUsuario() && SesionActual.getUsuario().getFavoritos() != null) {
            favsCount = SesionActual.getUsuario().getFavoritos().size();
        }
        if (SesionActual.hayUsuario() && SesionActual.getUsuario().getFavoritos() != null) {
            artistasCount = SesionActual.getUsuario().getFavoritos().size();
        }
        
        // Cargar los géneros restringidos reales del usuario de MongoDB
        List<String> generosRestringidosList = new ArrayList<>();
        if (SesionActual.hayUsuario() && SesionActual.getUsuario().getGenerosNoDeseados() != null) {
            generosRestringidosList.addAll(SesionActual.getUsuario().getGenerosNoDeseados());
        } else {
            // Datos de respaldo estéticos sólo si no hay conexión activa
            generosRestringidosList.add("Reggaeton");
            generosRestringidosList.add("Pop");
        }
        restringidosIniciales = generosRestringidosList.size();

        // ---- Barra superior ----
        JLabel back = new JLabel("\u2039");
        back.setFont(new Font("SansSerif", Font.BOLD, 18));
        back.setForeground(GRIS_TEXTO);
        back.setBounds(30, 22, 20, 24);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        content.add(back);

        JLabel breadcrumb = new JLabel("Perfil");
        breadcrumb.setFont(new Font("SansSerif", Font.BOLD, 14));
        breadcrumb.setForeground(TEXTO_OSCURO);
        breadcrumb.setBounds(55, 22, 150, 24);
        content.add(breadcrumb);

        // Grupo Esquina Superior Derecha (Avatar + Nombre + Flecha)
        JPanel userMini = crearAvatarCirculo(28, inicialesGrandes);
        content.add(userMini);

        JLabel userMiniName = new JLabel(nombreReal.split(" ")[0]); 
        userMiniName.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userMiniName.setForeground(TEXTO_OSCURO);
        content.add(userMiniName);

        // Flecha funcional v (\u2304)
        JLabel chevron = new JLabel("\u2304");
        chevron.setFont(new Font("SansSerif", Font.BOLD, 12));
        chevron.setForeground(GRIS_TEXTO);
        chevron.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        content.add(chevron);

        // =========================================================
        //  MENÚ DE LOGOUT DESPLEGABLE (FUNCIONAL AL DAR CLIC A LA FLECHA)
        // =========================================================
        JPopupMenu logoutMenu = new JPopupMenu();
        logoutMenu.setBackground(Color.WHITE);
        logoutMenu.setBorder(BorderFactory.createLineBorder(GRIS_BORDE, 1));
        
        JMenuItem logoutItem = new JMenuItem("Cerrar sesión \u21AA");
        logoutItem.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutItem.setForeground(ROSA_TEXTO);
        logoutItem.setBackground(Color.WHITE);
        logoutItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        logoutItem.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(
                parentFrame, 
                "¿Estás seguro de que deseas cerrar la sesión?", 
                "Cerrar Sesión", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (respuesta == JOptionPane.YES_OPTION) {
                SesionActual.cerrar(); // Resetea el DTO global
                parentFrame.dispose();  // Cierra el Frame del Perfil
                System.out.println("Sesión destruida. Redireccionando a la pantalla de Login...");
                // Aquí instancias tu vista del login:
                // new LoginFrame().setVisible(true);
            }
        });
        logoutMenu.add(logoutItem);

        // Evento para detonar el menú justo abajo del Chevron
        chevron.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logoutMenu.show(chevron, -80, chevron.getHeight() + 4);
            }
        });

        // ---- Datos del cuerpo de perfil ----
        JPanel avatarGrande = crearAvatarCirculo(80, inicialesGrandes);
        avatarGrande.setBounds(30, 85, 80, 80);
        content.add(avatarGrande);

        JLabel nombre = new JLabel(nombreReal);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 22));
        nombre.setForeground(TEXTO_OSCURO);
        nombre.setBounds(125, 95, 300, 30);
        content.add(nombre);

        JLabel correo = new JLabel(tagReal + "  \u00B7  " + correoReal);
        correo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        correo.setForeground(GRIS_TEXTO);
        correo.setBounds(125, 128, 450, 20);
        content.add(correo);

        // ---- Tarjetas de estadísticas con datos Dinámicos ----
        JPanel stats = new JPanel(null);
        stats.setOpaque(false);
        stats.setBounds(600, 95, 350, 70);
        
        JPanel cardFavs = crearTarjetaEstadistica(String.valueOf(favsCount), "Favoritos", 0);
        JPanel cardArtistas = crearTarjetaEstadistica(String.valueOf(artistasCount), "Artistas", 120);
        JPanel cardRestringidos = crearTarjetaEstadistica(String.valueOf(restringidosIniciales), "Restringidos", 240);
        
        // Recuperamos los punteros de los números internos de las tarjetas creadas
        lblNumFavoritos = (JLabel) cardFavs.getComponent(0);
        lblNumArtistas = (JLabel) cardArtistas.getComponent(0);
        lblNumRestringidos = (JLabel) cardRestringidos.getComponent(0);

        stats.add(cardFavs);
        stats.add(cardArtistas);
        stats.add(cardRestringidos);
        content.add(stats);
        statsPanelRef = stats;

        // Comportamiento Responsivo
        content.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = content.getWidth();
                userMini.setBounds(w - 150, 20, 28, 28);
                userMiniName.setBounds(w - 115, 24, 75, 20);
                chevron.setBounds(w - 35, 24, 16, 20);
                if (statsPanelRef != null) {
                    statsPanelRef.setBounds(w - 380, 95, 350, 70);
                }
            }
        });

        // ---- Línea divisoria ----
        JPanel linea = new JPanel();
        linea.setBackground(GRIS_BORDE);
        linea.setBounds(30, 195, 1000, 1);
        content.add(linea);

        // ---- Sección de Géneros ----
        JLabel tituloGeneros = new JLabel("G\u00e9neros no deseados");
        tituloGeneros.setFont(new Font("SansSerif", Font.BOLD, 15));
        tituloGeneros.setForeground(TEXTO_OSCURO);
        tituloGeneros.setBounds(30, 220, 300, 22);
        content.add(tituloGeneros);

        JLabel subTituloGeneros = new JLabel("No ver\u00e1s artistas ni \u00e1lbumes de estos g\u00e9neros en tu biblioteca.");
        subTituloGeneros.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subTituloGeneros.setForeground(GRIS_TEXTO);
        subTituloGeneros.setBounds(30, 244, 450, 18);
        content.add(subTituloGeneros);

        JPanel filaGeneros = new JPanel(null);
        filaGeneros.setOpaque(false);
        filaGeneros.setBounds(30, 275, 800, 40);
        content.add(filaGeneros);

        JComboBox<String> combo = new JComboBox<>(new String[]{"Metal", "Jazz", "Cl\u00e1sica", "Rock", "Country", "Reggaeton", "Pop"});
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setFocusable(false);

        // Redibujado con actualización dinámica en base de datos local (SesionActual)
        Runnable[] redibujar = new Runnable[1];
        redibujar[0] = () -> {
            filaGeneros.removeAll();
            int x = 0;
            for (String genero : new ArrayList<>(generosRestringidosList)) {
                JPanel chip = crearChipGenero(genero, () -> {
                    generosRestringidosList.remove(genero);
                    
                    // Persistencia funcional: Actualiza la lista en memoria del usuario de la sesión
                    if (SesionActual.hayUsuario()) {
                        SesionActual.getUsuario().setGenerosNoDeseados(generosRestringidosList);
                        // NOTA: Aquí puedes llamar a tu capa DAO/Service si deseas persistir inmediatamente en MongoDB:
                        // usuarioService.actualizar(SesionActual.getUsuario());
                    }
                    
                    // Actualiza el número de la tarjeta de inmediato
                    lblNumRestringidos.setText(String.valueOf(generosRestringidosList.size()));
                    redibujar[0].run();
                });
                chip.setBounds(x, 2, chip.getPreferredSize().width, 32);
                filaGeneros.add(chip);
                x += chip.getPreferredSize().width + 10;
            }
            
            combo.setBounds(x, 2, 130, 30);
            filaGeneros.add(combo);
            x += 140;

            JButton agregar = new JButton("+") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(MORADO);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("+", (getWidth() - fm.stringWidth("+")) / 2, (getHeight() / 2) + 5);
                    g2.dispose();
                }
            };
            agregar.setContentAreaFilled(false);
            agregar.setBorderPainted(false);
            agregar.setFocusPainted(false);
            agregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            agregar.setBounds(x, 1, 32, 32);
            
            // Acción para añadir elementos reales
            agregar.addActionListener(e -> {
                String seleccionado = (String) combo.getSelectedItem();
                if (seleccionado != null && !generosRestringidosList.contains(seleccionado)) {
                    generosRestringidosList.add(seleccionado);
                    
                    // Persistencia funcional: Vincula los cambios al objeto real del Usuario
                    if (SesionActual.hayUsuario()) {
                        SesionActual.getUsuario().setGenerosNoDeseados(generosRestringidosList);
                        // Opcional: tu fachada de negocio para actualizar tu documento de MongoDB:
                        // servicios.usuarios().actualizar(SesionActual.getUsuario());
                    }
                    
                    // Actualiza el número de la tarjeta al instante
                    lblNumRestringidos.setText(String.valueOf(generosRestringidosList.size()));
                    redibujar[0].run();
                }
            });
            
            filaGeneros.add(agregar);
            filaGeneros.revalidate();
            filaGeneros.repaint();
        };
        
        redibujar[0].run();
        return content;
    }

    static JPanel crearChipGenero(String texto, Runnable onRemove) {
        JPanel chip = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ROSA_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        chip.setOpaque(false);

        String textoCompleto = texto + "   \u00D7";
        JLabel lbl = new JLabel(textoCompleto);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(ROSA_TEXTO);
        
        FontMetrics metrics = lbl.getFontMetrics(lbl.getFont());
        int anchoTexto = metrics.stringWidth(textoCompleto);
        lbl.setBounds(12, 6, anchoTexto + 10, 20);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onRemove.run();
            }
        });
        chip.add(lbl);

        chip.setPreferredSize(new Dimension(anchoTexto + 24, 32));
        return chip;
    }

    static JPanel crearTarjetaEstadistica(String numero, String etiqueta, int x) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFAFAFA));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(GRIS_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(x, 0, 110, 70);

        JLabel num = new JLabel(numero);
        num.setFont(new Font("SansSerif", Font.BOLD, 18));
        num.setForeground(TEXTO_OSCURO);
        num.setHorizontalAlignment(SwingConstants.CENTER);
        num.setBounds(0, 12, 110, 24);
        card.add(num);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(GRIS_TEXTO);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBounds(0, 38, 110, 18);
        card.add(lbl);

        return card;
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