import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ImagenAbierta extends JFrame {
    private ModeloImagen modelo;
    private PanelImagen panelImagen;
    private PanelHerramientas panelHerramientas;

    public ImagenAbierta(BufferedImage imagen) {
        // Configuración de la ventana
        setTitle("Editor de Imagen");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Modelo con la imagen cargada
        modelo = new ModeloImagen(imagen);

        // Panel de imagen
        panelImagen = new PanelImagen(modelo, null, null);

        // Panel de herramientas
        panelHerramientas = new PanelHerramientas(modelo);

        // Agregar componentes a la ventana
        add(panelImagen, BorderLayout.CENTER);
        add(panelHerramientas, BorderLayout.EAST);

        setLocationRelativeTo(null); // Centrar ventana
    }
}
