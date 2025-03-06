package diagnostico;

class Nodo {
    String nombre;
    Nodo izquierdo, derecho;

    public Nodo(String nombre) {
        this.nombre = nombre;
        this.izquierdo = null;
        this.derecho = null;
    }
}

public class Arbol {
    Nodo raiz;

    public Arbol() {
        this.raiz = null;
    }

    public boolean vacio() {
        return raiz == null;
    }

    public Nodo buscarNodo(String nombre) {
        return buscarRecursivo(raiz, nombre);
    }

    private Nodo buscarRecursivo(Nodo actual, String nombre) {
        if (actual == null) {
            return null;
        }
        if (actual.nombre.equals(nombre)) {
            return actual;
        }
        Nodo izquierdo = buscarRecursivo(actual.izquierdo, nombre);
        return (izquierdo != null) ? izquierdo : buscarRecursivo(actual.derecho, nombre);
    }

    public static void main(String[] args) {
        Arbol arbol = new Arbol();
        System.out.println(arbol.vacio()); // true

        arbol.raiz = new Nodo("M");
        arbol.raiz.izquierdo = new Nodo("B");
        arbol.raiz.derecho = new Nodo("Q");

        System.out.println(arbol.vacio()); // false

        Nodo nodo = arbol.buscarNodo("B");
        if (nodo != null) {
            System.out.println("Nodo encontrado: " + nodo.nombre);
        } else {
            System.out.println("Nodo no encontrado");
        }
    }
}
