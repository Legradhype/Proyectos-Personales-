import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PanelImagen extends JPanel implements PropertyChangeListener {
    private ModeloImagen modelo;
    private PanelHerramientas panelHerramientas;
    private VentanaPrincipal ventanaPrincipal;
    private Point startPoint;
    private Point currentPoint;
    private boolean isDragging = false;

    public PanelImagen(ModeloImagen modelo, PanelHerramientas panelHerramientas, VentanaPrincipal ventanaPrincipal) {
        this.modelo = modelo;
        this.panelHerramientas = panelHerramientas;
        this.ventanaPrincipal = ventanaPrincipal;

        if (modelo != null) {
            modelo.addObserver(this);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (modelo == null) return;
                int x = e.getX();
                int y = e.getY();

                modelo.guardarEstadoActual(); // Guarda el estado actual antes de iniciar una nueva acción

                if (panelHerramientas.isModoPunto()) {
                    dibujarPunto(x, y);
                } else if (panelHerramientas.isModoLinea() || panelHerramientas.isModoRectangulo() || panelHerramientas.isModoCirculo()) {
                    startPoint = new Point(x, y);
                    currentPoint = startPoint;
                    isDragging = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (modelo == null || !isDragging) return;
                int x = e.getX();
                int y = e.getY();

                if (panelHerramientas.isModoLinea()) {
                    dibujarLinea(startPoint.x, startPoint.y, x, y);
                } else if (panelHerramientas.isModoRectangulo()) {
                    int ancho = x - startPoint.x;
                    int alto = y - startPoint.y;
                    dibujarRectangulo(startPoint.x, startPoint.y, ancho, alto);
                } else if (panelHerramientas.isModoCirculo()) {
                    int radio = (int) startPoint.distance(x, y);
                    dibujarCirculo(startPoint.x, startPoint.y, radio);
                }

                isDragging = false;
                startPoint = null;
                currentPoint = null;
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    currentPoint = new Point(e.getX(), e.getY());
                    repaint();
                }
            }
        });
    }

    private void dibujarPunto(int x, int y) {
        Graphics2D g2d = modelo.getImagen().createGraphics();
        g2d.setColor(modelo.getColorSeleccionado());
        g2d.drawLine(x, y, x, y);
        g2d.dispose();
        ventanaPrincipal.agregarAccionHistorial("Dibujar punto");
        modelo.notificarCambio();
    }

    private void dibujarLinea(int x1, int y1, int x2, int y2) {
        Graphics2D g2d = modelo.getImagen().createGraphics();
        g2d.setColor(modelo.getColorSeleccionado());
        g2d.drawLine(x1, y1, x2, y2);
        g2d.dispose();
        ventanaPrincipal.agregarAccionHistorial("Dibujar línea");
        modelo.notificarCambio();
    }

    private void dibujarRectangulo(int x, int y, int ancho, int alto) {
        Graphics2D g2d = modelo.getImagen().createGraphics();
        g2d.setColor(modelo.getColorSeleccionado());
        g2d.drawRect(x, y, ancho, alto);
        g2d.dispose();
        ventanaPrincipal.agregarAccionHistorial("Dibujar rectángulo");
        modelo.notificarCambio();
    }

    private void dibujarCirculo(int centroX, int centroY, int radio) {
        Graphics2D g2d = modelo.getImagen().createGraphics();
        g2d.setColor(modelo.getColorSeleccionado());
        g2d.drawOval(centroX - radio, centroY - radio, radio * 2, radio * 2);
        g2d.dispose();
        ventanaPrincipal.agregarAccionHistorial("Dibujar círculo");
        modelo.notificarCambio();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (modelo == null || modelo.getImagen() == null) return;

        // Dibuja la imagen actual en el panel
        g.drawImage(modelo.getImagen(), 0, 0, this.getWidth(), this.getHeight(), this);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("IMAGEN".equals(evt.getPropertyName())) {
            repaint(); // Redibuja el panel cuando la imagen cambia
        }
    }

    public void setModelo(ModeloImagen modelo) {
        this.modelo = modelo;
        repaint();
    }
}
