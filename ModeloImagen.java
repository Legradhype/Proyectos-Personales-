import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

public class ModeloImagen {
    private static final Logger logger = LogManager.getRootLogger();
    private BufferedImage imagen;
    private Color colorSeleccionado;
    private int rango = 10;
    private PropertyChangeSupport observado;
    private Pila<BufferedImage> historial;  // Uso de Pila personalizada en lugar de Stack

    // Constructor

    public ModeloImagen() {
        this.imagen = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        this.colorSeleccionado = Color.BLACK;
        this.observado = new PropertyChangeSupport(this);
        this.historial = new Pila<>();
        guardarEstadoActual();
    }

    public ModeloImagen(BufferedImage imagenCargada) {
        this();
        if (imagenCargada != null) {
            this.imagen = imagenCargada;
            guardarEstadoActual();
        }
    }

    public void guardarEstadoActual() {
        if (imagen != null) {
            BufferedImage copia = new BufferedImage(imagen.getWidth(), imagen.getHeight(), imagen.getType());
            Graphics g = copia.getGraphics();
            g.drawImage(imagen, 0, 0, null);
            g.dispose();
            historial.push(copia);
        }
    }


    public void limpiarHistorial() {
        historial.vaciar();
        logger.info("Historial de deshacer limpiado.");
    }

    public void restablecerImagen() {
        Graphics2D g2d = imagen.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, imagen.getWidth(), imagen.getHeight());
        g2d.dispose();
        notificarCambio();
        logger.info("Imagen restablecida a blanco.");
    }



    public void deshacer() {
        if (!historial.estaVacia()) {
            imagen = historial.pop();
            notificarCambio();
            logger.info("Se deshizo la última operación.");
        } else {
            logger.warn("No hay acciones para deshacer.");
        }
    }



    public void dibujarPunto(int x, int y) {
        guardarEstadoActual(); // Guarda el estado actual en el historial antes de cualquier cambio

        // Asegurarnos de que el punto esté dentro de los límites de la imagen
        if (x >= 0 && x < imagen.getWidth() && y >= 0 && y < imagen.getHeight()) {
            imagen.setRGB(x, y, colorSeleccionado.getRGB()); // Aplica el color seleccionado
            notificarCambio(); // Notifica a los observadores para que se actualice el panel
        }
    }



    public void dibujarLinea(int x1, int y1, int x2, int y2) {
        guardarEstadoActual();
        Graphics g = imagen.getGraphics();
        g.setColor(colorSeleccionado);  // Asegúrate de usar colorSeleccionado aquí
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
        notificarCambio();
    }

    public void dibujarRectangulo(int x, int y, int ancho, int alto) {
        guardarEstadoActual();
        int x2 = x + ancho;
        int y2 = y + alto;

        dibujarLinea(x, y, x2, y);  // Línea superior
        dibujarLinea(x, y, x, y2);  // Línea izquierda
        dibujarLinea(x2, y, x2, y2);  // Línea derecha
        dibujarLinea(x, y2, x2, y2);  // Línea inferior
        notificarCambio();
        logger.info(String.format("Dibujado rectángulo en (%d, %d) con ancho %d y alto %d.", x, y, ancho, alto));
    }

    public void dibujarCirculo(int centroX, int centroY, int radio) {
        guardarEstadoActual();

        int x = 0;
        int y = radio;
        int d = 1 - radio;

        dibujarPuntosCirculo(centroX, centroY, x, y);

        while (x < y) {
            if (d < 0) {
                d = d + 2 * x + 3;
            } else {
                d = d + 2 * (x - y) + 5;
                y--;
            }
            x++;
            dibujarPuntosCirculo(centroX, centroY, x, y);
        }

        notificarCambio();
    }

    private void dibujarPuntosCirculo(int centroX, int centroY, int x, int y) {
        int color = colorSeleccionado.getRGB();

        if (dentroDeLimites(centroX + x, centroY + y)) imagen.setRGB(centroX + x, centroY + y, color);
        if (dentroDeLimites(centroX - x, centroY + y)) imagen.setRGB(centroX - x, centroY + y, color);
        if (dentroDeLimites(centroX + x, centroY - y)) imagen.setRGB(centroX + x, centroY - y, color);
        if (dentroDeLimites(centroX - x, centroY - y)) imagen.setRGB(centroX - x, centroY - y, color);
        if (dentroDeLimites(centroX + y, centroY + x)) imagen.setRGB(centroX + y, centroY + x, color);
        if (dentroDeLimites(centroX - y, centroY + x)) imagen.setRGB(centroX - y, centroY + x, color);
        if (dentroDeLimites(centroX + y, centroY - x)) imagen.setRGB(centroX + y, centroY - x, color);
        if (dentroDeLimites(centroX - y, centroY - x)) imagen.setRGB(centroX - y, centroY - x, color);
    }

    private boolean dentroDeLimites(int x, int y) {
        return x >= 0 && x < imagen.getWidth() && y >= 0 && y < imagen.getHeight();
    }

    public void rellenar(int x, int y) {
        guardarEstadoActual();
        Color colorReferencia = new Color(imagen.getRGB(x, y));

        if (colorReferencia.equals(colorSeleccionado)) {
            logger.warn(String.format("El color seleccionado es igual al color de referencia en (%d, %d). No se realiza relleno.", x, y));
            return;
        }

        int width = imagen.getWidth();
        int height = imagen.getHeight();
        boolean[][] visitado = new boolean[width][height];

        Cola<Point> queue = new Cola<>();
        queue.agregar(new Point(x, y));

        while (!queue.estaVacia()) {
            Point p = queue.remover();
            int currentX = p.x;
            int currentY = p.y;

            if (currentX < 0 || currentX >= width || currentY < 0 || currentY >= height) continue;
            if (visitado[currentX][currentY]) continue;

            Color colorActual = new Color(imagen.getRGB(currentX, currentY));
            if (!estaDentroDelRango(colorActual, colorReferencia)) continue;

            imagen.setRGB(currentX, currentY, colorSeleccionado.getRGB());
            visitado[currentX][currentY] = true;

            queue.agregar(new Point(currentX + 1, currentY));
            queue.agregar(new Point(currentX - 1, currentY));
            queue.agregar(new Point(currentX, currentY + 1));
            queue.agregar(new Point(currentX, currentY - 1));
        }

        logger.info(String.format("Rellenado del área a partir de (%d, %d) con color %s.", x, y, colorSeleccionado));
        notificarCambio();
    }

    private boolean estaDentroDelRango(Color actual, Color referencia) {
        int diffR = Math.abs(actual.getRed() - referencia.getRed());
        int diffG = Math.abs(actual.getGreen() - referencia.getGreen());
        int diffB = Math.abs(actual.getBlue() - referencia.getBlue());
        return diffR <= rango && diffG <= rango && diffB <= rango;
    }

    public void aplicarFiltroBrillo(int incremento) {
        guardarEstadoActual();
        for (int x = 0; x < imagen.getWidth(); x++) {
            for (int y = 0; y < imagen.getHeight(); y++) {
                Color color = new Color(imagen.getRGB(x, y));
                int r = Math.min(255, Math.max(0, color.getRed() + incremento));
                int g = Math.min(255, Math.max(0, color.getGreen() + incremento));
                int b = Math.min(255, Math.max(0, color.getBlue() + incremento));
                imagen.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        notificarCambio();
        logger.info("Filtro de brillo aplicado con incremento de " + incremento);
    }

    public void reducirBrillo(int decremento) {
        aplicarFiltroBrillo(-decremento);
        logger.info("Filtro de brillo reducido en " + decremento);
    }

    public void aplicarFiltroBlancoNegro() {
        guardarEstadoActual();
        for (int x = 0; x < imagen.getWidth(); x++) {
            for (int y = 0; y < imagen.getHeight(); y++) {
                Color color = new Color(imagen.getRGB(x, y));
                int promedio = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                imagen.setRGB(x, y, new Color(promedio, promedio, promedio).getRGB());
            }
        }
        notificarCambio();
        logger.info("Filtro blanco y negro aplicado.");
    }

    public void notificarCambio() {
        observado.firePropertyChange("IMAGEN", true, false);
        logger.info("Se ha notificado un cambio en la imagen.");
    }

    public void addObserver(PropertyChangeListener observador) {
        observado.addPropertyChangeListener(observador);
        logger.info("Añadido nuevo observador.");
    }

    public void removeObserver(PropertyChangeListener observador) {
        observado.removePropertyChangeListener(observador);
        logger.info("Observador eliminado.");
    }

    public void setColorSeleccionado(Color colorSeleccionado) {
        this.colorSeleccionado = colorSeleccionado;
        logger.info("Color seleccionado cambiado a " + colorSeleccionado);
    }


    public Color getColorSeleccionado() {
        return this.colorSeleccionado;
    }

    public BufferedImage getImagen() {
        return imagen;
    }

    public void setRango(int rango) {
        this.rango = rango;
        logger.info("Rango de tolerancia de color cambiado a " + rango);
    }

    public int getRango() {
        return rango;
    }

    public void cargarImagen(String rutaArchivo) {
        try {
            BufferedImage nuevaImagen = ImageIO.read(new File(rutaArchivo));
            if (nuevaImagen != null) {
                // Convertir la imagen cargada a un formato compatible para edición
                BufferedImage imagenCompatible = new BufferedImage(
                        nuevaImagen.getWidth(),
                        nuevaImagen.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = imagenCompatible.createGraphics();
                g2d.drawImage(nuevaImagen, 0, 0, null);
                g2d.dispose();

                this.imagen = imagenCompatible;
                this.historial = new Pila<>();  // Reinicia el historial cuando cargas una nueva imagen
                guardarEstadoActual();
                notificarCambio();
                logger.info("Imagen cargada desde archivo: " + rutaArchivo);
            } else {
                logger.error("Error al cargar la imagen: archivo no encontrado o formato inválido.");
            }
        } catch (IOException e) {
            logger.error("Error al cargar la imagen desde archivo.", e);
        }
    }



    public void guardarImagen(String rutaArchivo) {
        try {
            String extension = obtenerExtension(rutaArchivo);
            if (extension == null || (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg"))) {
                logger.warn("Formato no soportado. Solo se permiten PNG y JPG.");
                return;
            }
            File archivoSalida = new File(rutaArchivo);
            ImageIO.write(imagen, extension, archivoSalida);
            logger.info("Imagen guardada exitosamente en: " + rutaArchivo);
        } catch (IOException e) {
            logger.error("Error al guardar la imagen: " + e.getMessage());
        }
    }

    private String obtenerExtension(String rutaArchivo) {
        int i = rutaArchivo.lastIndexOf('.');
        if (i > 0 && i < rutaArchivo.length() - 1) {
            return rutaArchivo.substring(i + 1).toLowerCase();
        }
        return null;
    }

}
