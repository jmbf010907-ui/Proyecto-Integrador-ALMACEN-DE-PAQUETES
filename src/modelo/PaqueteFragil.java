package modelo;

/** Paquete frágil: cargo fijo de manejo más 12 pesos por día desde el primer día. */
public class PaqueteFragil extends Paquete {
    public static final double TARIFA_DIA = 12.0;

    private double cargoManejo;

    public PaqueteFragil(String codigo, double pesoKg, Remitente remitente,
                         Destinatario destinatario, double cargoManejo) {
        super(codigo, pesoKg, remitente, destinatario); // usa el constructor de Paquete
        if (Double.isNaN(cargoManejo) || Double.isInfinite(cargoManejo) || cargoManejo < 0) {
            throw new IllegalArgumentException("El cargo de manejo no puede ser negativo.");
        }
        this.cargoManejo = cargoManejo; // this: atributo; cargoManejo: parámetro
    }

    public double getCargoManejo() { return cargoManejo; }

    @Override
    public double calcularCostoAlmacenaje(int dias) {
        validarDias(dias);
        return cargoManejo + dias * TARIFA_DIA;
    }

    @Override
    public String toString() {
        return super.toString() + " | Tipo: Frágil (manejo $" + cargoManejo + ")";
    }
}
