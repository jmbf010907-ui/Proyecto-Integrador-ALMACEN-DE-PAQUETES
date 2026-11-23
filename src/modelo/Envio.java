package modelo;

import java.time.LocalDate;

/** Registro de un paquete desde que entra hasta que sale del almacén. */
public class Envio {
    public static final String ACTIVO = "ACTIVO";
    public static final String CERRADO = "CERRADO";

    private Paquete paquete;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private String estado;
    private Entrega entrega;

    public Envio(Paquete paquete, LocalDate fechaIngreso) {
        if (paquete == null) {
            throw new IllegalArgumentException("El envío requiere un paquete.");
        }
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
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

    public boolean estaActivo() {
        return ACTIVO.equals(estado);
    }

    /**
     * Cierra el envío con su entrega. Primero valida todo (no cerrado, entrega válida,
     * fecha posterior al ingreso, paquete ubicado en un anaquel) y solo entonces cambia:
     * el envío pasa a CERRADO, el paquete a ENTREGADO y el anaquel queda libre.
     */
    public void cerrar(Entrega entrega) {
        if (!estaActivo()) {
            throw new IllegalStateException("El envío del paquete " + paquete.getCodigo() + " ya está cerrado.");
        }
        if (entrega == null) {
            throw new IllegalArgumentException("Se requiere una entrega para cerrar el envío.");
        }
        if (entrega.getFecha().isBefore(fechaIngreso)) {
            throw new IllegalArgumentException("La entrega no puede ser anterior a la fecha de ingreso.");
        }
        Anaquel anaquel = paquete.getAnaquel();
        if (anaquel == null || !paquete.estaEnAlmacen()) {
            throw new IllegalStateException("El paquete " + paquete.getCodigo() + " no está guardado en un anaquel.");
        }
        // A partir de aquí ya no hay validaciones que puedan fallar: se cambian los tres objetos.
        this.entrega = entrega;
        this.fechaSalida = entrega.getFecha();
        this.estado = CERRADO;
        paquete.marcarEntregado();
        anaquel.liberar();
    }

    @Override
    public String toString() {
        String texto = "Envío [" + paquete.getCodigo() + "] ingreso " + fechaIngreso + " | " + estado;
        if (entrega != null) {
            texto += " | " + entrega;
        }
        return texto;
    }
}
