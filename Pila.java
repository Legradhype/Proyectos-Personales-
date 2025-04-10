public class Pila<T> {
    private Nodo<T> tope;
    private int tamanio;

    private static class Nodo<T> {
        T valor;
        Nodo<T> siguiente;

        public Nodo(T valor) {
            this.valor = valor;
        }
    }

    public Pila() {
        this.tope = null;
        this.tamanio = 0;
    }

    public void push(T valor) {
        Nodo<T> nuevoNodo = new Nodo<>(valor);
        nuevoNodo.siguiente = tope;
        tope = nuevoNodo;
        tamanio++;
    }

    public T pop() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía");
        }
        T valor = tope.valor;
        tope = tope.siguiente;
        tamanio--;
        return valor;
    }


    public boolean estaVacia() {
        return tope == null;
    }

    // Método para vaciar la pila
    public void vaciar() {
        tope = null;
        tamanio = 0;
    }
}
