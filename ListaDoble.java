public class ListaDoble<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int longitud;

    protected static class Nodo<T> {
        T valor;
        Nodo<T> anterior;
        Nodo<T> siguiente;

        public Nodo(T valor) {
            this.valor = valor;
            this.anterior = null;
            this.siguiente = null;
        }
    }

    public ListaDoble() {
        this.cabeza = null;
        this.cola = null;
        this.longitud = 0;
    }

    public void agregarAlFinal(T valor) {
        Nodo<T> nuevoNodo = new Nodo<>(valor);
        if (estaVacia()) {
            cabeza = cola = nuevoNodo;
        } else {
            cola.siguiente = nuevoNodo;
            nuevoNodo.anterior = cola;
            cola = nuevoNodo;
        }
        longitud++;
    }

    public void eliminarEn(int indice) {
        if (indice < 0 || indice >= longitud) {
            throw new IndexOutOfBoundsException("Índice fuera de límites");
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }

        if (actual.anterior != null) {
            actual.anterior.siguiente = actual.siguiente;
        } else {
            cabeza = actual.siguiente;
        }

        if (actual.siguiente != null) {
            actual.siguiente.anterior = actual.anterior;
        } else {
            cola = actual.anterior;
        }

        longitud--;
    }


    public T obtener(int indice) {
        if (indice < 0 || indice >= longitud) {
            throw new IndexOutOfBoundsException("Índice fuera de límites");
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    public boolean estaVacia() {
        return longitud == 0;
    }

    public int getLongitud() {
        return longitud;
    }
}
