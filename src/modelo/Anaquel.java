package modelo;

/**
 * Lugar del almacén donde se guarda un paquete.
 * Mantiene la relación en los dos sentidos: si el anaquel tiene un paquete,
 * ese paquete apunta a este anaquel, y viceversa.
 */
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

    /**
     * Guarda el paquete. Valida todo antes de cambiar algo:
     * no acepta nulos, anaqueles ocupados, paquetes ya ubicados en otro anaquel ni entregados.
     */
    public void ocupar(Paquete p) {
        if (p == null) {
            throw new IllegalArgumentException("No se puede ocupar un anaquel con un paquete nulo.");
        }
        if (!estaLibre()) {
            throw new IllegalStateException("El anaquel " + codigo + " ya está ocupado.");
        }
        if (p.getAnaquel() != null) {
            throw new IllegalStateException("El paquete " + p.getCodigo() + " ya está en el anaquel "
                    + p.getAnaquel().getCodigo() + ".");
        }
        if (!p.estaEnAlmacen()) {
            throw new IllegalStateException("El paquete " + p.getCodigo() + " ya fue entregado.");
        }
        this.paquete = p;
        p.asignarAnaquel(this);
    }

    /** No se puede liberar un anaquel que ya está libre. */
    public void liberar() {
        if (estaLibre()) {
            throw new IllegalStateException("El anaquel " + codigo + " ya está libre.");
        }
        paquete.asignarAnaquel(null);
        this.paquete = null;
    }

    @Override
    public String toString() {
        return codigo + (estaLibre() ? " [libre]" : " [ocupado por " + paquete.getCodigo() + "]");
    }
}
