import java.awt.*;

public class DibujarPuntoComando implements Comando {
    private ModeloImagen modelo;
    private int x, y;
    private Color colorAnterior;

    public DibujarPuntoComando(ModeloImagen modelo, int x, int y) {
        this.modelo = modelo;
        this.x = x;
        this.y = y;
        this.colorAnterior = new Color(modelo.getImagen().getRGB(x, y));  // Guardar el color original antes de hacer el cambio
    }

    @Override
    public void ejecutar() {
        modelo.dibujarPunto(x, y);  // Ejecuta el comando de dibujar el punto
    }

    @Override
    public void deshacer() {
        modelo.getImagen().setRGB(x, y, colorAnterior.getRGB());  // Restaura el color original
        modelo.notificarCambio();  // Notificar que la imagen ha cambiado
    }
}
