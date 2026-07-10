/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion;

import com.equipo3.bibliotecamusical.dtos.CredencialesDTO;
import com.equipo3.bibliotecamusical.dtos.UsuarioDTO;
import com.equipo3.bibliotecamusical.negocio.excepciones.AutenticacionException;
import com.equipo3.bibliotecamusical.negocio.excepciones.NegocioException;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import com.equipo3.bibliotecamusical.negocio.servicios.AutenticacionService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
/**
 * Interfaz completa de "Biblioteca Musical": pantalla de Iniciar Sesion
 * y pantalla de Crear Cuenta, en un unico archivo.
 *
 * Al ejecutar se abre primero Iniciar Sesion. El enlace "Crear Cuenta"
 * abre la pantalla de registro, y desde ahi "Inicia sesion" regresa
 * al login. Solo contiene la interfaz visual (sin logica de negocio).
 */
public class InicioSesion {

    // Instancia del servicio que procesará el login
    private static AutenticacionService autenticacionService;

    // ---------- Punto de entrada ----------
    public static void main(String[] args) {
        // NOTA: Recuerda inicializar 'autenticacionService' antes de hacer pruebas,
        // pasándole tu implementación de UsuarioDAOImpl conectada a MongoDB.
        SwingUtilities.invokeLater(() -> crearLogin().setVisible(true));
    }

    // =========================================================
    //  PANTALLA: INICIAR SESION
    // =========================================================
    static JFrame crearLogin() {
        JFrame frame = new JFrame("Biblioteca Musical - Iniciar Sesion");
        frame.setSize(760, 480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(crearSidebar(), BorderLayout.WEST);

        JPanel content = new JPanel(null);
        content.setBackground(Color.WHITE);

        JLabel title = new JLabel("Iniciar Sesion");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(0x1A1A2E));
        title.setBounds(45, 60, 300, 35);
        content.add(title);

        JLabel userLbl = new JLabel("Nombre de usuario o Correo");
        userLbl.setForeground(new Color(0x555555));
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLbl.setBounds(45, 115, 250, 18);
        content.add(userLbl);

        // Se deja vacío para que el usuario escriba limpiamente
        JTextField userField = crearCampoTexto(""); 
        userField.setBounds(45, 135, 360, 38);
        content.add(userField);

        JLabel passLbl = new JLabel("Contraseña");
        passLbl.setForeground(new Color(0x555555));
        passLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passLbl.setBounds(45, 185, 250, 18);
        content.add(passLbl);

        JPanel passPanel = crearCampoPassword();
        passPanel.setBounds(45, 205, 360, 38);
        content.add(passPanel);
        
        // Extraemos el JPasswordField real desde el panel para leer su contenido
        JPasswordField passField = (JPasswordField) passPanel.getComponent(0);

        JButton loginBtn = crearBotonRedondeado("Iniciar Sesion", new Color(0x18E5B0));
        loginBtn.setBounds(45, 270, 360, 42);
        
        // --- EVENTO DE INICIO DE SESIÓN INTEGRADO Y SIN ERRORES ---
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            // Empaquetamos los datos en el DTO
            CredencialesDTO credenciales = new CredencialesDTO(username, password);

            if (autenticacionService != null) {
                try {
                    // 1. Llamada protegida en bloque try-catch (Soluciona error 1)
                    UsuarioDTO usuarioLogueado = autenticacionService.login(credenciales);
                    
                    // 2. Uso correcto de la sintaxis de Record empleando .nombreUsuario() (Soluciona error 2)
                    JOptionPane.showMessageDialog(frame, "¡Bienvenido " + usuarioLogueado.nombreUsuario() + "!", "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
                    
                    frame.dispose();
                    // Aquí abrirías tu MenuPrincipal: 
                    // new MenuPrincipal().setVisible(true);

                } catch (AutenticacionException | ValidacionException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error de Credenciales", JOptionPane.WARNING_MESSAGE);
                } catch (NegocioException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error del Sistema", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "El servicio de autenticación no está inicializado.", "Error de Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(loginBtn);

        JLabel link = new JLabel("<html>¿No tienes cuenta? "
                + "<font color='#18C29A'><b>Crear Cuenta</b></font></html>");
        link.setFont(new Font("SansSerif", Font.PLAIN, 12));
        link.setBounds(45, 325, 300, 20);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                crearRegistro().setVisible(true);
            }
        });
        content.add(link);

        frame.add(content, BorderLayout.CENTER);
        return frame;
    }

    // =========================================================
    //  PANTALLA: CREAR CUENTA
    // =========================================================
    static JFrame crearRegistro() {
        JFrame frame = new JFrame("Biblioteca Musical - Crear Cuenta");
        frame.setSize(760, 480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(crearSidebar(), BorderLayout.WEST);

        JPanel content = new JPanel(null);
        content.setBackground(Color.WHITE);

        JLabel title = new JLabel("Crea tu cuenta");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(0x1A1A2E));
        title.setBounds(45, 30, 300, 32);
        content.add(title);

        JLabel subtitle = new JLabel("Regístrate para completar el proceso");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(0x999999));
        subtitle.setBounds(45, 62, 320, 18);
        content.add(subtitle);

        JPanel photoCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF1F1F5));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0x999999));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                String s = "\uD83D\uDCF7";
                g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, getHeight() / 2 + 7);
                g2.dispose();
            }
        };
        photoCircle.setOpaque(false);
        photoCircle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        photoCircle.setBounds(190, 90, 50, 50);
        content.add(photoCircle);

        JLabel photoLbl = new JLabel("Foto de perfil");
        photoLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        photoLbl.setForeground(new Color(0x999999));
        photoLbl.setHorizontalAlignment(SwingConstants.CENTER);
        photoLbl.setBounds(145, 142, 140, 16);
        content.add(photoLbl);

        JLabel userLbl = new JLabel("Nombre de usuario");
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLbl.setForeground(new Color(0x555555));
        userLbl.setBounds(45, 168, 170, 18);
        content.add(userLbl);

        JTextField userField = crearCampoTexto("");
        userField.setBounds(45, 188, 170, 36);
        content.add(userField);

        JLabel emailLbl = new JLabel("Correo electrónico");
        emailLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        emailLbl.setForeground(new Color(0x555555));
        emailLbl.setBounds(235, 168, 170, 18);
        content.add(emailLbl);

        JTextField emailField = crearCampoTexto("");
        emailField.setBounds(235, 188, 170, 36);
        content.add(emailField);

        JLabel passLbl = new JLabel("Contraseña");
        passLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passLbl.setForeground(new Color(0x555555));
        passLbl.setBounds(45, 235, 250, 18);
        content.add(passLbl);

        JPanel passField = crearCampoPassword();
        passField.setBounds(45, 255, 360, 36);
        content.add(passField);

        JButton registerBtn = crearBotonRedondeado("Crear cuenta", new Color(0x18E5B0));
        registerBtn.setBounds(45, 305, 360, 42);
        content.add(registerBtn);

        JLabel link = new JLabel("<html>¿Ya tienes cuenta? "
                + "<font color='#18C29A'><b>Inicia sesión</b></font></html>");
        link.setFont(new Font("SansSerif", Font.PLAIN, 12));
        link.setBounds(45, 360, 300, 20);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                crearLogin().setVisible(true);
            }
        });
        content.add(link);

        frame.add(content, BorderLayout.CENTER);
        return frame;
    }

    // =========================================================
    //  COMPONENTES REUTILIZABLES
    // =========================================================

    static JPanel crearSidebar() {
        JPanel sidebar = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x8E2DE2), 0, getHeight(), new Color(0x4A00E0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sidebar.setPreferredSize(new Dimension(300, 480));
        sidebar.setOpaque(false);

        JLabel back = new JLabel("\u2190");
        back.setForeground(Color.WHITE);
        back.setFont(new Font("SansSerif", Font.BOLD, 20));
        back.setBounds(20, 18, 30, 30);
        sidebar.add(back);

        JLabel title = new JLabel("Biblioteca Musical");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBounds(55, 20, 220, 25);
        sidebar.add(title);

        JPanel art = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(0x3949AB));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 60));
                FontMetrics fm = g2.getFontMetrics();
                String s = "\u266A\u266B";
                int x = (getWidth() - fm.stringWidth(s)) / 2;
                int y = getHeight() / 2 + 20;
                g2.drawString(s, x, y);
                g2.dispose();
            }
        };
        art.setOpaque(false);
        art.setBounds(15, 70, 270, 260);
        sidebar.add(art);

        JPanel bottomBar = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x3B0764));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bottomBar.setBounds(0, 440, 300, 40);
        JLabel home = new JLabel("\u2302");
        home.setForeground(Color.WHITE);
        home.setFont(new Font("SansSerif", Font.PLAIN, 18));
        home.setBounds(20, 8, 30, 24);
        bottomBar.add(home);
        sidebar.add(bottomBar);

        return sidebar;
    }

    static JTextField crearCampoTexto(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF1F1F5));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g3.setColor(new Color(0xAFAFAF));
                    g3.setFont(getFont());
                    g3.drawString(placeholder, 15, getHeight() / 2 + 5);
                    g3.dispose();
                }
            }
        };
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return field;
    }

    static JPanel crearCampoPassword() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF1F1F5));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);

        JPasswordField field = new JPasswordField();
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 5));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setEchoChar('\u2022');
        panel.add(field, BorderLayout.CENTER);

        JLabel eye = new JLabel("\u25CF\u25CF");
        eye.setForeground(new Color(0x999999));
        eye.setFont(new Font("SansSerif", Font.PLAIN, 12));
        eye.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eye.addMouseListener(new MouseAdapter() {
            boolean visible = false;
            @Override
            public void mouseClicked(MouseEvent e) {
                visible = !visible;
                field.setEchoChar(visible ? (char) 0 : '\u2022');
                eye.setForeground(visible ? new Color(0x18C29A) : new Color(0x999999));
            }
        });
        panel.add(eye, BorderLayout.EAST);

        return panel;
    }

    static JButton crearBotonRedondeado(String texto, Color color) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}