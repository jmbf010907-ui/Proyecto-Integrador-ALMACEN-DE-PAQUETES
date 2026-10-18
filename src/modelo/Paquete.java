package modelo;

/** Envío individual guardado en el almacén. */
public class Paquete {
    public static final String EN_ALMACEN = "EN_ALMACEN";
    public static final String ENTREGADO = "ENTREGADO";

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
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0.");
        }
        if (remitente == null || destinatario == null) {
            throw new IllegalArgumentException("Se requieren remitente y destinatario.");
        }
        this.codigo = codigo;
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

    public void setAnaquel(Anaquel anaquel) { this.anaquel = anaquel; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        String ubic = (anaquel == null) ? "sin anaquel" : anaquel.getCodigo();
        return "Paquete " + codigo + " | " + pesoKg + " kg | " + estado + " | " + ubic
                + " | De: " + remitente.getNombre() + " | Para: " + destinatario.getNombre();
    }
}
