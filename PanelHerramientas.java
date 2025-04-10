import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class PanelHerramientas extends JPanel {
    private static final Logger logger = LogManager.getRootLogger();
    private ModeloImagen modelo;
    private JColorChooser selectorColor;  // Paleta de colores
    private JToggleButton botonPunto;
    private JToggleButton botonLinea;
    private JToggleButton botonRectangulo;
    private JToggleButton botonCirculo;
    private JToggleButton botonRelleno;
    private JToggleButton botonCubetita;
    private JToggleButton botonRecursivo;
    private JSlider sliderRango;

    private boolean modoPunto;
    private boolean modoLinea;
    private boolean modoRectangulo;
    private boolean modoCirculo;
    private boolean modoRelleno;
    private boolean modoRecursivo;

    public PanelHerramientas(ModeloImagen modelo) {
        this.modelo = modelo;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Configurar el selector de color
        selectorColor = new JColorChooser();  // Sin Color.BLACK
        selectorColor.getSelectionModel().addChangeListener(e -> {
            Color nuevoColor = selectorColor.getColor();
            modelo.setColorSeleccionado(nuevoColor);  // Actualizar el color en el modelo
            logger.info("Color seleccionado cambiado a: " + nuevoColor);
        });

        // Botón para activar el modo recursivo
        botonRecursivo = new JToggleButton("Modo Recursivo");
        botonRecursivo.addActionListener(e -> {
            modoRecursivo = botonRecursivo.isSelected();
            logger.info("Modo recursivo activado: " + modoRecursivo);
        });

        // Botón para dibujar un punto
        botonPunto = new JToggleButton("Punto");
        botonPunto.addActionListener(e -> {
            modoPunto = botonPunto.isSelected();
            desactivarOtrosModos(); // Desactivar otros modos
            logger.info("Modo punto activado: " + modoPunto);
        });

        // Botón para dibujar una línea
        botonLinea = new JToggleButton("Línea");
        botonLinea.addActionListener(e -> {
            modoLinea = botonLinea.isSelected();
            desactivarOtrosModos();
            logger.info("Modo línea activado: " + modoLinea);
        });

        // Botón para dibujar un rectángulo
        botonRectangulo = new JToggleButton("Rectángulo");
        botonRectangulo.addActionListener(e -> {
            modoRectangulo = botonRectangulo.isSelected();
            desactivarOtrosModos();
            logger.info("Modo rectángulo activado: " + modoRectangulo);
        });

        // Botón para dibujar un círculo
        botonCirculo = new JToggleButton("Círculo");
        botonCirculo.addActionListener(e -> {
            modoCirculo = botonCirculo.isSelected();
            desactivarOtrosModos();
            logger.info("Modo círculo activado: " + modoCirculo);
        });

        // Botón para activar el relleno
        botonRelleno = new JToggleButton("Relleno");
        botonRelleno.addActionListener(e -> {
            modoRelleno = botonRelleno.isSelected();
            desactivarOtrosModos();
            logger.info("Modo relleno activado: " + modoRelleno);
        });

        // Botón para rellenar con cubetita
        botonCubetita = new JToggleButton("Cubetita");
        botonCubetita.addActionListener(e -> {
            modoRelleno = botonCubetita.isSelected();
            desactivarOtrosModos();
            logger.info("Modo cubetita activado: " + modoRelleno);
        });

        // Slider para ajustar el rango del color en el relleno
        sliderRango = new JSlider(0, 255, modelo.getRango());
        sliderRango.setMajorTickSpacing(50);
        sliderRango.setMinorTickSpacing(10);
        sliderRango.setPaintTicks(true);
        sliderRango.setPaintLabels(true);
        sliderRango.addChangeListener(e -> {
            int nuevoRango = sliderRango.getValue();
            modelo.setRango(nuevoRango);
            logger.info("Rango de color ajustado a: " + nuevoRango);
        });

        // Añadir componentes a la interfaz
        add(new JLabel("Selecciona un color:"));
        add(selectorColor);
        add(new JLabel("Herramientas de Dibujo:"));
        add(botonPunto);
        add(botonLinea);
        add(botonRectangulo);
        add(botonCirculo);
        add(botonRelleno);
        add(botonCubetita);
        add(new JLabel("Ajustar rango de color:"));
        add(sliderRango);
    }

    // Métodos para obtener el estado de cada modo
    public boolean isModoPunto() {
        return modoPunto;
    }

    public boolean isModoLinea() {
        return modoLinea;
    }

    public boolean isModoRectangulo() {
        return modoRectangulo;
    }

    public boolean isModoCirculo() {
        return modoCirculo;
    }

    public boolean isModoRelleno() {
        return modoRelleno;
    }

    public boolean isModoCubetita() {
        return botonCubetita.isSelected();
    }

    // Método para obtener el color seleccionado
    public Color getColorSeleccionado() {
        return selectorColor.getColor();
    }

    // Desactivar otros modos cuando uno es seleccionado
    private void desactivarOtrosModos() {
        if (!modoPunto) botonPunto.setSelected(false);
        if (!modoLinea) botonLinea.setSelected(false);
        if (!modoRectangulo) botonRectangulo.setSelected(false);
        if (!modoCirculo) botonCirculo.setSelected(false);
        if (!modoRelleno) botonRelleno.setSelected(false);
        if (!modoRelleno) botonCubetita.setSelected(false);
    }
}
