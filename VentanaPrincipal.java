import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {
    private static final Logger logger = LogManager.getRootLogger();
    private ListaDoble<ModeloImagen> listaImagenes;
    private int indiceActual;
    private PanelImagen panelImagen;
    private PanelHerramientas panelHerramientas;
    private DefaultListModel<String> modeloHistorialVisual;
    private JList<String> listaHistorial;

    public VentanaPrincipal() {
        setTitle("Editor de Multi-Imagenes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicializa la lista de imágenes con una imagen en blanco
        listaImagenes = new ListaDoble<>();
        ModeloImagen imagenInicial = new ModeloImagen();
        listaImagenes.agregarAlFinal(imagenInicial);
        indiceActual = 0;

        panelHerramientas = new PanelHerramientas(imagenInicial);
        panelImagen = new PanelImagen(imagenInicial, panelHerramientas, this);
        panelImagen.setOpaque(false);

        add(panelImagen, BorderLayout.CENTER);
        add(panelHerramientas, BorderLayout.EAST);

        modeloHistorialVisual = new DefaultListModel<>();
        listaHistorial = new JList<>(modeloHistorialVisual);
        JScrollPane scrollPane = new JScrollPane(listaHistorial);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        add(scrollPane, BorderLayout.WEST);

        JPanel panelImagenes = crearPanelBotones();
        add(panelImagenes, BorderLayout.NORTH);

        JMenuBar menuBar = crearMenu();
        setJMenuBar(menuBar);

        pack();
        setVisible(true);
        logger.info("Ventana principal de multi-imagenes creada.");
    }

    private JPanel crearPanelBotones() {
        JPanel panelImagenes = new JPanel();

        JButton botonEliminarImagen = new JButton("Eliminar Imagen");
        botonEliminarImagen.addActionListener(e -> {
            if (!listaImagenes.estaVacia()) {
                listaImagenes.eliminarEn(indiceActual);
                if (!listaImagenes.estaVacia()) {
                    indiceActual = Math.min(indiceActual, listaImagenes.getLongitud() - 1);
                    ModeloImagen imagenActual = listaImagenes.obtener(indiceActual);
                    panelImagen.setModelo(imagenActual);
                    panelImagen.repaint();
                } else {
                    ModeloImagen nuevaImagen = new ModeloImagen();
                    listaImagenes.agregarAlFinal(nuevaImagen);
                    indiceActual = 0;
                    panelImagen.setModelo(nuevaImagen);
                }
            }
        });

        JButton botonAvanzar = new JButton("Siguiente Imagen");
        botonAvanzar.addActionListener(e -> avanzarImagen());

        JButton botonRetroceder = new JButton("Imagen Anterior");
        botonRetroceder.addActionListener(e -> retrocederImagen());

        JButton botonDeshacer = new JButton("Deshacer");
        botonDeshacer.addActionListener(e -> {
            listaImagenes.obtener(indiceActual).deshacer();
            panelImagen.repaint();
            agregarAccionHistorial("Deshacer última acción");
        });

        panelImagenes.add(botonEliminarImagen);
        panelImagenes.add(botonAvanzar);
        panelImagenes.add(botonRetroceder);
        panelImagenes.add(botonDeshacer);

        return panelImagenes;
    }

    private void avanzarImagen() {
        if (indiceActual < listaImagenes.getLongitud() - 1) {
            indiceActual++;
            ModeloImagen siguiente = listaImagenes.obtener(indiceActual);
            panelImagen.setModelo(siguiente);
            limpiarHistorialVisual();
            panelImagen.repaint();
            agregarAccionHistorial("Pasando a la imagen posterior");
        }
    }

    private void retrocederImagen() {
        if (indiceActual > 0) {
            indiceActual--;
            ModeloImagen anterior = listaImagenes.obtener(indiceActual);
            panelImagen.setModelo(anterior);
            limpiarHistorialVisual();
            panelImagen.repaint();
            agregarAccionHistorial("Pasando a la imagen anterior");
        }
    }

    public void agregarAccionHistorial(String accion) {
        modeloHistorialVisual.addElement(accion);
        logger.info("Acción agregada al historial: " + accion);
    }

    private JMenuBar crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem abrirItem = new JMenuItem(new AbstractAction("Abrir") {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirImagen();
            }
        });

        JMenuItem guardarItem = new JMenuItem(new AbstractAction("Guardar") {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarImagen();
            }
        });

        JMenuItem salirItem = new JMenuItem(new AbstractAction("Salir") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuArchivo.add(abrirItem);
        menuArchivo.add(guardarItem);
        menuArchivo.add(salirItem);
        menuBar.add(menuArchivo);

        return menuBar;
    }

    private void abrirImagen() {
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            try {
                BufferedImage nuevaImagen = ImageIO.read(archivo);

                if (nuevaImagen == null) {
                    JOptionPane.showMessageDialog(this, "Formato de imagen no soportado o archivo corrupto.",
                            "Error de Carga", JOptionPane.ERROR_MESSAGE);
                    logger.error("No se pudo abrir la imagen: formato no soportado o archivo corrupto.");
                    return;
                }

                ModeloImagen modeloImagen = new ModeloImagen(nuevaImagen);
                listaImagenes.agregarAlFinal(modeloImagen);
                indiceActual = listaImagenes.getLongitud() - 1;
                panelImagen.setModelo(modeloImagen);
                limpiarHistorialVisual();
                panelImagen.repaint();
                logger.info("Imagen cargada desde archivo: " + archivo.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al abrir la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
                logger.error("Error al abrir la imagen.", e);
            }
        }
    }

    private void guardarImagen() {
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".png")) {
                ruta += ".png";
            }
            listaImagenes.obtener(indiceActual).guardarImagen(ruta);
            logger.info("Imagen guardada en: " + ruta);
        }
    }

    private void limpiarHistorialVisual() {
        modeloHistorialVisual.clear();
        logger.info("Historial visual limpiado.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
