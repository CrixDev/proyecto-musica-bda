package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Barra de busqueda reutilizable (campo redondeado con icono de lupa y texto
 * de ayuda) usada por las tres pantallas de consulta. Notifica en cada pulsacion
 * de tecla para poder filtrar en vivo.
 */
public class BarraBusqueda extends JPanel {

    private static final Color FONDO_CAMPO = new Color(0xF1, 0xF1, 0xF5);

    private final JTextField campo;
    private final String placeholder;

    public BarraBusqueda(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "Buscar";
        setLayout(new BorderLayout(8, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));

        JLabel lupa = new JLabel("🔍");
        lupa.setForeground(Estilos.TEXTO_SECUNDARIO);
        add(lupa, BorderLayout.WEST);

        campo = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0xAF, 0xAF, 0xB6));
                    g2.setFont(getFont());
                    g2.drawString(BarraBusqueda.this.placeholder, 2, getHeight() / 2 + getFontMetrics(getFont()).getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        campo.setOpaque(false);
        campo.setBorder(null);
        campo.setFont(Estilos.TEXTO_NORMAL);
        campo.setForeground(Estilos.TEXTO_PRIMARIO);
        campo.setCaretColor(Estilos.TEXTO_PRIMARIO);
        add(campo, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(FONDO_CAMPO);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
        g2.dispose();
        super.paintComponent(g);
    }

    /** Registra un listener que se dispara en cada cambio del texto (busqueda en vivo). */
    public void alEscribir(Consumer<String> listener) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                listener.accept(campo.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                listener.accept(campo.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                listener.accept(campo.getText());
            }
        });
    }

    /** Registra un listener adicional para cuando el usuario presiona Enter. */
    public void alEnter(Consumer<String> listener) {
        ActionListener al = e -> listener.accept(campo.getText());
        campo.addActionListener(al);
    }

    public String getTexto() {
        return campo.getText();
    }

    public void setTexto(String texto) {
        campo.setText(texto);
    }

    public void enfocar() {
        campo.requestFocusInWindow();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = 42;
        return d;
    }
}
