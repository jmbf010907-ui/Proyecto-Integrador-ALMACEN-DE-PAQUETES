package modelo;

/** Espacio físico del almacén donde se guarda un paquete. Sabe si está libre u ocupado. */
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

    /** Un anaquel está libre cuando no guarda ningún paquete. */
    public boolean estaLibre() {
        return paquete == null;
    }

    /**
     * Guarda un paquete en el anaquel. Valida ANTES de cambiar el estado: si la
     * operación se rechaza, el anaquel queda exactamente como estaba.
     *
     * @throws IllegalArgumentException si el paquete es nulo
     * @throws IllegalStateException    si el anaquel ya está ocupado
     */
    public void ocupar(Paquete p) {
        if (p == null) {
            throw new IllegalArgumentException("No se puede ocupar un anaquel con un paquete nulo.");
        }
        if (!estaLibre()) {
            throw new IllegalStateException("El anaquel " + codigo + " ya está ocupado.");
        }
        this.paquete = p;
    }

    /**
     * Deja el anaquel libre para recibir otro paquete.
     *
     * @throws IllegalStateException si el anaquel ya estaba libre (cambio de estado inválido)
     */
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
