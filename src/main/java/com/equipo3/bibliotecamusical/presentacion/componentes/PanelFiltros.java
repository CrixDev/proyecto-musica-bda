package com.equipo3.bibliotecamusical.presentacion.componentes;

import com.equipo3.bibliotecamusical.entidades.Genero;
import com.equipo3.bibliotecamusical.negocio.servicios.CriteriosBusqueda;
import com.equipo3.bibliotecamusical.negocio.servicios.OrdenBusqueda;
import com.equipo3.bibliotecamusical.negocio.servicios.TipoContenido;
import com.equipo3.bibliotecamusical.presentacion.estilo.Estilos;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * Popup de filtros del buscador: tipo de contenido (solo artistas/albumes/
 * canciones), orden, anio y genero. Al aplicar, entrega unos
 * {@link CriteriosBusqueda} (sin texto; la pantalla que lo invoca conserva su
 * propio texto de busqueda).
 */
public final class PanelFiltros {

    private static final String TODOS = "Todos";

    private PanelFiltros() {
    }

    /**
     * Construye y muestra el popup de filtros anclado a {@code ancla}.
     *
     * @param ancla             componente bajo el cual se despliega el popup.
     * @param actuales          criterios actuales, para preseleccionar los combos.
     * @param aniosDisponibles  anios presentes en los datos (para el combo de anio).
     * @param permitirTipo      si {@code true} se muestra el combo de tipo de contenido.
     * @param alAplicar         callback con los nuevos criterios (texto vacio).
     */
    public static void mostrar(Component ancla, CriteriosBusqueda actuales,
            List<Integer> aniosDisponibles, boolean permitirTipo,
            Consumer<CriteriosBusqueda> alAplicar) {

        CriteriosBusqueda base = actuales != null ? actuales : CriteriosBusqueda.vacio();

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(Estilos.BORDE, 1));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Estilos.FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JComboBox<TipoContenido> comboTipo = new JComboBox<>(TipoContenido.values());
        comboTipo.setSelectedItem(base.tipo() != null ? base.tipo() : TipoContenido.TODOS);
        if (permitirTipo) {
            panel.add(fila("Mostrar", comboTipo));
            panel.add(Box.createVerticalStrut(10));
        }

        JComboBox<OrdenBusqueda> comboOrden = new JComboBox<>(OrdenBusqueda.values());
        comboOrden.setSelectedItem(base.orden() != null ? base.orden() : OrdenBusqueda.RELEVANCIA);
        panel.add(fila("Ordenar por", comboOrden));
        panel.add(Box.createVerticalStrut(10));

        JComboBox<String> comboAnio = new JComboBox<>();
        comboAnio.addItem(TODOS);
        if (aniosDisponibles != null) {
            for (Integer anio : aniosDisponibles) {
                comboAnio.addItem(String.valueOf(anio));
            }
        }
        comboAnio.setSelectedItem(base.anio() != null ? String.valueOf(base.anio()) : TODOS);
        panel.add(fila("Año", comboAnio));
        panel.add(Box.createVerticalStrut(10));

        JComboBox<String> comboGenero = new JComboBox<>();
        comboGenero.addItem(TODOS);
        for (String g : Genero.nombres()) {
            comboGenero.addItem(g);
        }
        comboGenero.setSelectedItem(base.genero() != null && !base.genero().isBlank() ? base.genero() : TODOS);
        panel.add(fila("Género", comboGenero));
        panel.add(Box.createVerticalStrut(14));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        botones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton limpiar = new JButton("Limpiar");
        limpiar.setFocusPainted(false);
        limpiar.addActionListener(e -> {
            popup.setVisible(false);
            alAplicar.accept(new CriteriosBusqueda(
                    "",
                    permitirTipo ? TipoContenido.TODOS : base.tipo(),
                    OrdenBusqueda.RELEVANCIA, null, null));
        });

        BotonPildora aplicar = new BotonPildora("Aplicar", true);
        aplicar.addActionListener(e -> {
            popup.setVisible(false);
            TipoContenido tipo = permitirTipo ? (TipoContenido) comboTipo.getSelectedItem() : base.tipo();
            OrdenBusqueda orden = (OrdenBusqueda) comboOrden.getSelectedItem();
            String anioSel = (String) comboAnio.getSelectedItem();
            Integer anio = (anioSel == null || anioSel.equals(TODOS)) ? null : Integer.valueOf(anioSel);
            String generoSel = (String) comboGenero.getSelectedItem();
            String genero = (generoSel == null || generoSel.equals(TODOS)) ? null : generoSel;
            alAplicar.accept(new CriteriosBusqueda("", tipo, orden, anio, genero));
        });

        botones.add(limpiar);
        botones.add(aplicar);
        panel.add(botones);

        popup.add(panel);
        popup.show(ancla, 0, ancla.getHeight() + 4);
    }

    private static JPanel fila(String etiqueta, JComboBox<?> combo) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.X_AXIS));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Estilos.TEXTO_NORMAL);
        lbl.setForeground(Estilos.TEXTO_SECUNDARIO);
        lbl.setPreferredSize(new Dimension(90, 26));
        lbl.setMaximumSize(new Dimension(90, 26));
        fila.add(lbl);
        fila.add(Box.createHorizontalStrut(10));

        combo.setFont(Estilos.TEXTO_NORMAL);
        combo.setFocusable(false);
        combo.setMaximumSize(new Dimension(200, 28));
        combo.setPreferredSize(new Dimension(200, 28));
        fila.add(combo);
        return fila;
    }
}
