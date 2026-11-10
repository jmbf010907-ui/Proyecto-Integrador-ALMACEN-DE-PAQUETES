package modelo;

import java.time.LocalDate;

/** Salida del paquete: fecha y quién lo recibió. */
public class Entrega {
    private LocalDate fecha;
    private String recibidoPor;

    public Entrega(LocalDate fecha, String recibidoPor) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de entrega es obligatoria.");
        }
        if (recibidoPor == null || recibidoPor.isBlank()) {
            throw new IllegalArgumentException("Se debe indicar quién recibe el paquete.");
        }
        this.fecha = fecha;
        this.recibidoPor = recibidoPor.trim();
    }

    public LocalDate getFecha() { return fecha; }
    public String getRecibidoPor() { return recibidoPor; }

    @Override
    public String toString() {
        return "Entrega el " + fecha + " a " + recibidoPor;
    }
}
