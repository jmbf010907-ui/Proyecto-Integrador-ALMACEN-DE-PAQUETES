package modelo;

import java.time.LocalDate;

/** Trámite de un paquete desde su ingreso hasta su salida del almacén. */
public class Envio {
    public static final String ACTIVO = "ACTIVO";
    public static final String CERRADO = "CERRADO";

    private Paquete paquete;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private String estado;
    private Entrega entrega;

    public Envio(Paquete paquete, LocalDate fechaIngreso) {
        if (paquete == null || fechaIngreso == null) {
            throw new IllegalArgumentException("Paquete y fecha de ingreso son obligatorios.");
        }
        this.paquete = paquete;
        this.fechaIngreso = fechaIngreso;
        this.estado = ACTIVO;
    }

    public Paquete getPaquete() { return paquete; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public String getEstado() { return estado; }
    public Entrega getEntrega() { return entrega; }
    public boolean estaActivo() { return ACTIVO.equals(estado); }

    /** Cierra el envío registrando la entrega. */
    public void cerrar(Entrega entrega) {
        if (!estaActivo()) {
            throw new IllegalStateException("El envío ya está cerrado.");
        }
        if (entrega.getFecha().isBefore(fechaIngreso)) {
            throw new IllegalArgumentException("La entrega no puede ser anterior al ingreso.");
        }
        this.entrega = entrega;
        this.fechaSalida = entrega.getFecha();
        this.estado = CERRADO;
    }

    @Override
    public String toString() {
        return "Envío [" + paquete.getCodigo() + "] ingreso " + fechaIngreso + " | " + estado
                + (entrega != null ? " | " + entrega : "");
    }
}
