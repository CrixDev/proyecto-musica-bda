/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.equipo3.bibliotecamusical.presentacion.estilo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 * Resuelve el campo {@code imagen}/{@code imagenPortada} de artistas y albumes
 * (que segun el requerimiento 7 puede ser una ruta fisica o venir embebida) y
 * genera un placeholder cuando no hay imagen disponible, para que las pantallas
 * nunca se queden con un hueco en blanco.
 *
 * <p>
 * Formatos soportados en el campo de imagen:
 * <ul>
 * <li>Ruta absoluta o relativa a un archivo en disco (p.ej.
 * {@code assets/img/artistas/x.png}).</li>
 * <li>Data URI base64 ({@code data:image/png;base64,...}), si el equipo decide
 * guardar la imagen embebida en el documento de Mongo.</li>
 * <li>{@code null} o vacio: se genera un placeholder con las iniciales.</li>
 * </ul>
 * 
 * @author Dylan
 */
public final class Imagenes {

    private static final int CACHE_MAX = 64;
    private static final java.util.Map<String, BufferedImage> CACHE
            = new java.util.LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<String, BufferedImage> eldest) {
            return size() > CACHE_MAX;
        }
    };

    private Imagenes() {
    }

    /**
     * Carga la imagen referenciada, o {@code null} si no se pudo resolver.
     */
    public static BufferedImage cargar(String referencia) {
        if (referencia == null || referencia.isBlank()) {
            return null;
        }
        BufferedImage cacheada = CACHE.get(referencia);
        if (cacheada != null) {
            return cacheada;
        }
        try {
            BufferedImage img;
            if (referencia.startsWith("data:")) {
                String base64 = referencia.substring(referencia.indexOf(',') + 1);
                byte[] bytes = Base64.getDecoder().decode(base64);
                img = ImageIO.read(new java.io.ByteArrayInputStream(bytes));
            } else if (referencia.startsWith("http://") || referencia.startsWith("https://")) {
                img = ImageIO.read(URI.create(referencia).toURL());
            } else {
                File archivo = new File(referencia);
                img = archivo.exists() ? ImageIO.read(archivo) : null;
            }
            if (img != null) {
                CACHE.put(referencia, img);
            }
            return img;
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Genera un placeholder cuadrado con degradado y las iniciales de
     * {@code texto}.
     */
    public static BufferedImage placeholder(String texto, int lado) {
        BufferedImage img = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, Estilos.ACENTO_MORADO_CLARO, lado, lado, Estilos.ACENTO_TEAL));
        g.fillRect(0, 0, lado, lado);
        g.setColor(new Color(255, 255, 255, 235));
        String iniciales = iniciales(texto);
        g.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, lado / 3)));
        var metricas = g.getFontMetrics();
        int x = (lado - metricas.stringWidth(iniciales)) / 2;
        int y = (lado - metricas.getHeight()) / 2 + metricas.getAscent();
        g.drawString(iniciales, x, y);
        g.dispose();
        return img;
    }

    /**
     * Recorta una imagen (o su placeholder si es null) en un circulo de
     * {@code lado} px.
     */
    public static BufferedImage circular(BufferedImage origen, String textoPlaceholder, int lado) {
        BufferedImage fuente = origen != null ? origen : placeholder(textoPlaceholder, lado);
        BufferedImage salida = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = salida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(new Ellipse2D.Float(0, 0, lado, lado));
        g.drawImage(escalarYRecortar(fuente, lado, lado), 0, 0, null);
        g.dispose();
        return salida;
    }

    /**
     * Recorta una imagen (o su placeholder) en un rectangulo con esquinas
     * redondeadas.
     */
    public static BufferedImage redondeada(BufferedImage origen, String textoPlaceholder,
            int ancho, int alto, int radio) {
        BufferedImage fuente = origen != null ? origen : placeholder(textoPlaceholder, Math.max(ancho, alto));
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = salida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(new RoundRectangle2D.Float(0, 0, ancho, alto, radio, radio));
        g.drawImage(escalarYRecortar(fuente, ancho, alto), 0, 0, null);
        g.dispose();
        return salida;
    }

    private static BufferedImage escalarYRecortar(BufferedImage fuente, int ancho, int alto) {
        double escala = Math.max((double) ancho / fuente.getWidth(), (double) alto / fuente.getHeight());
        int w = (int) Math.ceil(fuente.getWidth() * escala);
        int h = (int) Math.ceil(fuente.getHeight() * escala);
        BufferedImage escalada = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = escalada.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(fuente, 0, 0, w, h, null);
        g.dispose();
        BufferedImage recorte = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = recorte.createGraphics();
        g2.drawImage(escalada, -(w - ancho) / 2, -(h - alto) / 2, null);
        g2.dispose();
        return recorte;
    }

    private static String iniciales(String texto) {
        if (texto == null || texto.isBlank()) {
            return "?";
        }
        String[] partes = texto.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, partes.length); i++) {
            sb.append(Character.toUpperCase(partes[i].charAt(0)));
        }
        return sb.toString();
    }
}
