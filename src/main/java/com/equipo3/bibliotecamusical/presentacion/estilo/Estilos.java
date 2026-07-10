/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.estilo;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta de colores y tipografias usadas por las pantallas Swing, tomadas del
 * storyboard de Figma ("Biblioteca Musical"). Centralizar estos valores aqui
 * evita que cada pantalla nueva (la mia o la de mis companeros) invente su
 * propia paleta y que la app termine luciendo inconsistente.
 * 
 * @author Dylan
 */
public final class Estilos {

    private Estilos() {
    }

    // --- Fondo y superficies ---
    public static final Color FONDO = Color.WHITE;
    public static final Color SUPERFICIE = new Color(0xFA, 0xFA, 0xFB);
    public static final Color BORDE = new Color(0xE7, 0xE7, 0xEC);

    // --- Acentos de marca (ver banner y botones "Reproducir") ---
    public static final Color ACENTO_TEAL = new Color(0x2D, 0xD4, 0xBF);
    public static final Color ACENTO_TEAL_OSCURO = new Color(0x14, 0xB8, 0xA6);
    public static final Color ACENTO_MORADO = new Color(0x8B, 0x5C, 0xF6);
    public static final Color ACENTO_MORADO_CLARO = new Color(0xC4, 0xB5, 0xFD);

    // --- Banner degradado de cabecera (negro -> morado oscuro) ---
    public static final Color BANNER_INICIO = new Color(0x0B, 0x0B, 0x12);
    public static final Color BANNER_FIN = new Color(0x2B, 0x1F, 0x45);

    // --- Insignias (badges) tipo "Banda Indie" / "Album - rock" ---
    public static final Color BADGE_VERDE_FONDO = new Color(0xDC, 0xF5, 0xE3);
    public static final Color BADGE_VERDE_TEXTO = new Color(0x1F, 0x9D, 0x55);
    public static final Color BADGE_MORADO_FONDO = new Color(0xED, 0xE6, 0xFB);
    public static final Color BADGE_MORADO_TEXTO = new Color(0x7C, 0x3A, 0xED);

    // --- Texto ---
    public static final Color TEXTO_PRIMARIO = new Color(0x18, 0x18, 0x1F);
    public static final Color TEXTO_SECUNDARIO = new Color(0x6B, 0x6B, 0x76);
    public static final Color TEXTO_SOBRE_BANNER = Color.WHITE;

    // --- Favoritos ---
    public static final Color CORAZON_ACTIVO = ACENTO_MORADO;
    public static final Color CORAZON_INACTIVO = new Color(0xB9, 0xB9, 0xC4);

    // --- Tipografias ---
    public static final Font TITULO_GRANDE = new Font("SansSerif", Font.BOLD, 32);
    public static final Font TITULO_MEDIANO = new Font("SansSerif", Font.BOLD, 20);
    public static final Font SUBTITULO = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font TEXTO_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font TEXTO_NEGRITA = new Font("SansSerif", Font.BOLD, 13);
    public static final Font TEXTO_PEQUENO = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font BADGE_FUENTE = new Font("SansSerif", Font.BOLD, 11);
}
