package modelo;

/** Espacio físico del almacén donde se guarda un paquete. */
public class Anaquel {
    private String codigo;
    private Paquete paquete; // null si está libre

    public Anaquel(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del anaquel no puede estar vacío.");
        }
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
    public Paquete getPaquete() { return paquete; }
    public boolean estaLibre() { return paquete == null; }

    public void ocupar(Paquete p) {
        if (!estaLibre()) {
            throw new IllegalStateException("El anaquel " + codigo + " ya está ocupado.");
        }
        if (p == null) {
            throw new IllegalArgumentException("El paquete no puede ser nulo.");
        }
        this.paquete = p;
    }

    public void liberar() { this.paquete = null; }

    @Override
    public String toString() {
        return codigo + (estaLibre() ? " [libre]" : " [ocupado por " + paquete.getCodigo() + "]");
    }
}
