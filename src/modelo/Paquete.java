package modelo;

/**
 * Clase base: paquete guardado en el almacén. Las subclases cambian el costo de almacenaje.
 *
 * El estado y el anaquel NO tienen setters públicos: solo los cambian Anaquel (al ocupar o
 * liberar) y Envio (al cerrar con una entrega), que están en este mismo paquete y validan
 * antes de modificar. Así nadie puede asignar un texto de estado inventado ni una
 * ubicación que no coincida con el anaquel real.
 */
public class Paquete {
    public static final String EN_ALMACEN = "EN_ALMACEN";
    public static final String ENTREGADO = "ENTREGADO";
    private static final double TARIFA_GENERAL = 5.0;

    private String codigo;
    private double pesoKg;
    private String estado;
    private Remitente remitente;
    private Destinatario destinatario;
    private Anaquel anaquel;

    public Paquete(String codigo, double pesoKg, Remitente remitente, Destinatario destinatario) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del paquete no puede estar vacío.");
        }
        if (Double.isNaN(pesoKg) || Double.isInfinite(pesoKg) || pesoKg <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0.");
        }
        if (remitente == null || destinatario == null) {
            throw new IllegalArgumentException("Se requieren remitente y destinatario.");
        }
        this.codigo = codigo.trim();
        this.pesoKg = pesoKg;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.estado = EN_ALMACEN;
    }

    public String getCodigo() { return codigo; }
    public double getPesoKg() { return pesoKg; }
    public String getEstado() { return estado; }
    public Remitente getRemitente() { return remitente; }
    public Destinatario getDestinatario() { return destinatario; }
    public Anaquel getAnaquel() { return anaquel; }

    public boolean estaEnAlmacen() {
        return EN_ALMACEN.equals(estado);
    }

    /** Solo lo llama Anaquel.ocupar / Anaquel.liberar (visibilidad de paquete). */
    void asignarAnaquel(Anaquel anaquel) {
        this.anaquel = anaquel;
    }

    /** Solo lo llama Envio.cerrar después de validar la entrega (visibilidad de paquete). */
    void marcarEntregado() {
        if (!estaEnAlmacen()) {
            throw new IllegalStateException("El paquete " + codigo + " ya fue entregado.");
        }
        this.estado = ENTREGADO;
    }

    /** Método que las subclases sobrescriben: costo de guardar el paquete varios días. */
    public double calcularCostoAlmacenaje(int dias) {
        validarDias(dias);
        return dias * TARIFA_GENERAL;
    }

    protected void validarDias(int dias) {
        if (dias < 0) {
            throw new IllegalArgumentException("Los días de almacenaje no pueden ser negativos.");
        }
    }

    @Override
    public String toString() {
        String ubic = (anaquel == null) ? "sin anaquel" : anaquel.getCodigo();
        return "Paquete " + codigo + " | " + pesoKg + " kg | " + estado + " | " + ubic
                + " | De: " + remitente.getNombre() + " | Para: " + destinatario.getNombre();
    }
}
