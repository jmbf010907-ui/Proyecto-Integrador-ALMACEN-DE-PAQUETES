package modelo;

/** Lugar del almacén donde se guarda un paquete. */
public class Anaquel {
    private String codigo;
    private Paquete paquete;

    public Anaquel(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del anaquel no puede estar vacío.");
        }
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
    public Paquete getPaquete() { return paquete; }

    public boolean estaLibre() {
        return paquete == null;
    }

    /** No acepta paquetes nulos ni anaqueles ocupados. */
    public void ocupar(Paquete p) {
        if (p == null) {
            throw new IllegalArgumentException("No se puede ocupar un anaquel con un paquete nulo.");
        }
        if (!estaLibre()) {
            throw new IllegalStateException("El anaquel " + codigo + " ya está ocupado.");
        }
        this.paquete = p;
    }

    /** No se puede liberar un anaquel que ya está libre. */
    public void liberar() {
        if (estaLibre()) {
            throw new IllegalStateException("El anaquel " + codigo + " ya está libre.");
        }
        this.paquete = null;
    }

    @Override
    public String toString() {
        return codigo + (estaLibre() ? " [libre]" : " [ocupado por " + paquete.getCodigo() + "]");
    }
}
