public class Cola<T> {
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    private static class Nodo<T> {
        T valor;
        Nodo<T> siguiente;

        public Nodo(T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    public Cola() {
        this.primero = null;
        this.ultimo = null;
        this.tamanio = 0;
    }

    // Método para agregar un elemento al final de la cola
    public void agregar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            primero = nuevoNodo;
        } else {
            ultimo.siguiente = nuevoNodo;
        }
        ultimo = nuevoNodo;
        tamanio++;
    }

    // Método para remover el primer elemento de la cola
    public T remover() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía");
        }
        T valor = primero.valor;
        primero = primero.siguiente;
        tamanio--;
        if (primero == null) {
            ultimo = null;
        }
        return valor;
    }

    // Método para verificar si la cola está vacía
    public boolean estaVacia() {
        return tamanio == 0;
    }

    // Método para obtener el tamaño actual de la cola
    public int getTamanio() {
        return tamanio;
    }
}
