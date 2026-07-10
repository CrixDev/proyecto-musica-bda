package com.equipo3.bibliotecamusical.presentacion;

import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.negocio.Servicios;
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import com.equipo3.bibliotecamusical.persistencia.ConexionMongo;
import com.equipo3.bibliotecamusical.persistencia.InicializadorBd;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JTextField;
import org.bson.types.ObjectId;
import com.equipo3.bibliotecamusical.presentacion.vistas.NavegacionVistas;
import com.equipo3.bibliotecamusical.presentacion.vistas.PantallaAlbumes;
import com.equipo3.bibliotecamusical.presentacion.vistas.PantallaArtistas;
import com.equipo3.bibliotecamusical.presentacion.vistas.PantallaInicio;
import com.equipo3.bibliotecamusical.presentacion.vistas.VistaAlbumPanel;
import com.equipo3.bibliotecamusical.presentacion.vistas.VistaArtistaPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Shell principal de la aplicación: barra lateral de navegación + área de
 * contenido con {@link CardLayout} que hospeda las tres pantallas de consulta
 * (Inicio, Artistas, Álbumes) y las vistas de detalle (artista / álbum). Es el
 * punto de entrada gráfico real de la app una vez iniciada la sesión.
 *
 * <p>Ejecutar con:
 * {@code mvn -q compile exec:java
 * -Dexec.mainClass=com.equipo3.bibliotecamusical.presentacion.VentanaPrincipal}
 */
public class VentanaPrincipal {

    private static final Color MORADO = new Color(0x7C, 0x3A, 0xED);
    private static final Color MORADO_CLARO = new Color(0xF1, 0xE9, 0xFE);
    private static final Color GRIS_TEXTO = new Color(0x8A, 0x8A, 0x93);
    private static final Color GRIS_BORDE = new Color(0xE5, 0xE5, 0xEA);
    private static final Color TEXTO_OSCURO = new Color(0x1A, 0x1A, 0x2E);

    private final Servicios servicios;
    private final JFrame frame;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final Map<String, NavItem> items = new LinkedHashMap<>();

    private final PantallaInicio pantallaInicio;
    private final PantallaArtistas pantallaArtistas;
    private final PantallaAlbumes pantallaAlbumes;
    private final VistaArtistaPanel vistaArtista;
    private final VistaAlbumPanel vistaAlbum;

    private String ultimaSeccion = "inicio";

    public VentanaPrincipal(Servicios servicios) {
        this.servicios = servicios;

        frame = new JFrame("Biblioteca Musical");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 680));
        frame.setLayout(new BorderLayout());

        NavegacionVistas navegacion = new NavegacionVistas() {
            @Override
            public void irADetalleAlbum(String albumId) {
                mostrarDetalleAlbum(albumId);
            }

            @Override
            public void irADetalleArtista(String artistaId) {
                mostrarDetalleArtista(artistaId);
            }

            @Override
            public void volver() {
                mostrarSeccion(ultimaSeccion);
            }
        };

        pantallaInicio = new PantallaInicio(servicios, navegacion);
        pantallaArtistas = new PantallaArtistas(servicios, navegacion);
        pantallaAlbumes = new PantallaAlbumes(servicios, navegacion);
        vistaArtista = new VistaArtistaPanel(servicios, navegacion);
        vistaAlbum = new VistaAlbumPanel(servicios, navegacion);

        cards.add(pantallaInicio, "inicio");
        cards.add(pantallaArtistas, "artistas");
        cards.add(pantallaAlbumes, "albumes");
        cards.add(vistaArtista, "detalleArtista");
        cards.add(vistaAlbum, "detalleAlbum");

        frame.add(construirSidebar(), BorderLayout.WEST);
        frame.add(cards, BorderLayout.CENTER);

        mostrarSeccion("inicio");
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    // ------------------------------------------------------------- navegación

    private void mostrarSeccion(String clave) {
        switch (clave) {
            case "inicio" -> pantallaInicio.recargar();
            case "artistas" -> pantallaArtistas.recargar();
            case "albumes" -> pantallaAlbumes.recargar();
            default -> {
            }
        }
        ultimaSeccion = clave;
        cardLayout.show(cards, clave);
        marcarActivo(clave);
    }

    private void mostrarDetalleArtista(String artistaId) {
        vistaArtista.cargar(artistaId);
        cardLayout.show(cards, "detalleArtista");
        marcarActivo("artistas");
    }

    private void mostrarDetalleAlbum(String albumId) {
        vistaAlbum.cargar(albumId);
        cardLayout.show(cards, "detalleAlbum");
        marcarActivo("albumes");
    }

    private void marcarActivo(String clave) {
        items.forEach((k, item) -> item.setActivo(k.equals(clave)));
    }

    // --------------------------------------------------------------- sidebar

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(232, 680));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, GRIS_BORDE));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new javax.swing.BoxLayout(top, javax.swing.BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(18, 16, 0, 16));

        top.add(construirLogo());
        top.add(javax.swing.Box.createVerticalStrut(24));

        top.add(navItem("inicio", "⌂", "Inicio", () -> mostrarSeccion("inicio")));
        top.add(javax.swing.Box.createVerticalStrut(6));
        top.add(navItem("buscar", "🔍", "Buscar", () -> {
            mostrarSeccion("inicio");
            pantallaInicio.enfocarBusqueda();
        }));
        top.add(javax.swing.Box.createVerticalStrut(6));
        top.add(navItem("artistas", "🎤", "Artistas", () -> mostrarSeccion("artistas")));
        top.add(javax.swing.Box.createVerticalStrut(6));
        top.add(navItem("albumes", "💿", "Álbumes", () -> mostrarSeccion("albumes")));

        top.add(javax.swing.Box.createVerticalStrut(24));
        JLabel seccion = new JLabel("TU BIBLIOTECA");
        seccion.setFont(new Font("SansSerif", Font.BOLD, 10));
        seccion.setForeground(GRIS_TEXTO);
        seccion.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        seccion.setBorder(BorderFactory.createEmptyBorder(0, 6, 8, 0));
        top.add(seccion);

        top.add(navItem("favoritos", "♡", "Favoritos", this::abrirFavoritos));
        top.add(javax.swing.Box.createVerticalStrut(6));
        top.add(navItem("perfil", "👤", "Perfil", this::abrirPerfil));

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(construirChipUsuario(), BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel construirLogo() {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(220, 42));
        fila.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        JPanel icono = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x36, 0xD1, 0xDC), getWidth(), getHeight(), MORADO));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                String s = "♪";
                g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, getHeight() / 2 + 6);
                g2.dispose();
            }
        };
        icono.setOpaque(false);
        icono.setPreferredSize(new Dimension(38, 38));
        fila.add(icono, BorderLayout.WEST);

        JLabel txt = new JLabel("<html><b>Biblioteca</b><br><b>Musical</b></html>");
        txt.setFont(new Font("SansSerif", Font.BOLD, 14));
        txt.setForeground(TEXTO_OSCURO);
        fila.add(txt, BorderLayout.CENTER);
        return fila;
    }

    private NavItem navItem(String clave, String icono, String texto, Runnable accion) {
        NavItem item = new NavItem(icono, texto, accion);
        items.put(clave, item);
        return item;
    }

    private JPanel construirChipUsuario() {
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        String nombre = SesionActual.hayUsuario()
                ? SesionActual.getUsuario().getNombreUsuario() : "Invitado";
        String tag = "@" + nombre.toLowerCase().replace(" ", "_");
        String iniciales = nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();

        bottom.add(avatar(36, iniciales), BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new javax.swing.BoxLayout(textos, javax.swing.BoxLayout.Y_AXIS));
        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNombre.setForeground(TEXTO_OSCURO);
        JLabel lblTag = new JLabel(tag);
        lblTag.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTag.setForeground(GRIS_TEXTO);
        textos.add(lblNombre);
        textos.add(lblTag);
        bottom.add(textos, BorderLayout.CENTER);
        return bottom;
    }

    private JPanel avatar(int diametro, String iniciales) {
        JPanel av = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0xF6, 0xA6, 0xB2), getWidth(), getHeight(),
                        new Color(0x95, 0x75, 0xCD)));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, diametro / 3)));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(iniciales, (getWidth() - fm.stringWidth(iniciales)) / 2,
                        getHeight() / 2 + fm.getAscent() / 3);
                g2.dispose();
            }
        };
        av.setOpaque(false);
        av.setPreferredSize(new Dimension(diametro, diametro));
        return av;
    }

    private void abrirPerfil() {
        try {
            PerfilFrame.crearPerfil().setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(frame, "No se pudo abrir el perfil: " + e.getMessage());
        }
    }

    private void abrirFavoritos() {
        try {
            FavoritosFrame.crearFavoritos().setVisible(true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(frame, "No se pudo abrir favoritos: " + e.getMessage());
        }
    }

    // ------------------------------------------------------- item de menú

    /** Ítem de la barra lateral: icono + texto, con resaltado cuando está activo. */
    private final class NavItem extends JPanel {

        private final JLabel icono;
        private final JLabel texto;
        private boolean activo;

        NavItem(String iconoTxt, String textoTxt, Runnable accion) {
            setLayout(new BorderLayout(10, 0));
            setOpaque(false);
            setMaximumSize(new Dimension(220, 40));
            setAlignmentX(JPanel.LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            icono = new JLabel(iconoTxt);
            icono.setFont(new Font("SansSerif", Font.PLAIN, 14));
            icono.setForeground(GRIS_TEXTO);
            add(icono, BorderLayout.WEST);

            texto = new JLabel(textoTxt);
            texto.setFont(new Font("SansSerif", Font.PLAIN, 13));
            texto.setForeground(new Color(0x44, 0x44, 0x44));
            add(texto, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    accion.run();
                }
            });
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            icono.setForeground(activo ? MORADO : GRIS_TEXTO);
            texto.setForeground(activo ? MORADO : new Color(0x44, 0x44, 0x44));
            texto.setFont(new Font("SansSerif", activo ? Font.BOLD : Font.PLAIN, 13));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (activo) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MORADO_CLARO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    // -------------------------------------------------- login simulado (demo)

    /**
     * Muestra una pantalla de login <b>simulada</b> y, al "iniciar sesión" con
     * cualquier credencial, abre la ventana principal con una sesión de demo.
     * El login real (validación contra la BD) queda pendiente; esto solo sirve
     * para poder probar el resto de las pantallas ya conectadas.
     */
    public static void mostrarLoginSimulado(Servicios servicios) {
        JFrame login = new JFrame("Biblioteca Musical - Iniciar Sesión (demo)");
        login.setSize(760, 480);
        login.setResizable(false);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setLayout(new BorderLayout());
        login.add(InicioSesion.crearSidebar(), BorderLayout.WEST);

        JPanel content = new JPanel(null);
        content.setBackground(Color.WHITE);

        JLabel title = new JLabel("Iniciar Sesión");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXTO_OSCURO);
        title.setBounds(45, 55, 300, 35);
        content.add(title);

        JLabel userLbl = new JLabel("Nombre de usuario o Correo");
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLbl.setForeground(new Color(0x55, 0x55, 0x55));
        userLbl.setBounds(45, 110, 250, 18);
        content.add(userLbl);

        JTextField userField = InicioSesion.crearCampoTexto("Escribe cualquier usuario (demo)");
        userField.setBounds(45, 130, 360, 38);
        content.add(userField);

        JLabel passLbl = new JLabel("Contraseña");
        passLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passLbl.setForeground(new Color(0x55, 0x55, 0x55));
        passLbl.setBounds(45, 180, 250, 18);
        content.add(passLbl);

        JPanel passPanel = InicioSesion.crearCampoPassword();
        passPanel.setBounds(45, 200, 360, 38);
        content.add(passPanel);

        JButton loginBtn = InicioSesion.crearBotonRedondeado("Iniciar Sesión", new Color(0x18, 0xE5, 0xB0));
        loginBtn.setBounds(45, 265, 360, 42);
        loginBtn.addActionListener(e -> {
            simularSesion(userField.getText());
            login.dispose();
            new VentanaPrincipal(servicios).mostrar();
        });
        content.add(loginBtn);

        JLabel nota = new JLabel("<html><i>Modo demo: el login aún no valida contra la base de datos. "
                + "Entra con cualquier usuario/contraseña.</i></html>");
        nota.setFont(new Font("SansSerif", Font.PLAIN, 11));
        nota.setForeground(GRIS_TEXTO);
        nota.setBounds(45, 320, 370, 40);
        content.add(nota);

        login.add(content, BorderLayout.CENTER);
        login.getRootPane().setDefaultButton(loginBtn);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }

    /** Crea una sesión de demostración en memoria (sin persistir) para el usuario indicado. */
    private static void simularSesion(String nombreIngresado) {
        String nombre = (nombreIngresado == null || nombreIngresado.isBlank())
                ? "Cristian Devora" : nombreIngresado.trim();
        Usuario u = new Usuario();
        u.setId(new ObjectId());
        u.setNombreUsuario(nombre);
        u.setCorreo(nombre.toLowerCase().replace(" ", "_") + "@demo.com");
        u.setFechaRegistro(LocalDate.now());
        u.setFavoritos(new ArrayList<>());
        u.setGenerosNoDeseados(new ArrayList<>());
        SesionActual.iniciar(u);
    }

    // ------------------------------------------------------------------ main

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!ConexionMongo.disponible()) {
                JOptionPane.showMessageDialog(null,
                        "MongoDB no está disponible en localhost:27017.\n"
                        + "Inicia el servidor (o MongoDB Compass) y vuelve a intentar.",
                        "Biblioteca Musical", JOptionPane.ERROR_MESSAGE);
                return;
            }
            InicializadorBd.inicializar(ConexionMongo.getBaseDatos());
            Servicios servicios = new Servicios(ConexionMongo.getBaseDatos());
            // Fondo consistente con la paleta de la app.
            javax.swing.UIManager.put("Panel.background", Estilos.FONDO);
            new VentanaPrincipal(servicios).mostrar();
        });
    }
}
