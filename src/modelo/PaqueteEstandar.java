package modelo;

/** Paquete común: 3 días gratis y después 5 pesos por día. */
public class PaqueteEstandar extends Paquete {
    public static final int DIAS_GRATIS = 3;
    public static final double TARIFA_DIA = 5.0;

    public PaqueteEstandar(String codigo, double pesoKg, Remitente remitente, Destinatario destinatario) {
        super(codigo, pesoKg, remitente, destinatario); // usa el constructor de Paquete
    }

    @Override
    public double calcularCostoAlmacenaje(int dias) {
        validarDias(dias);
        int diasCobrados = Math.max(0, dias - DIAS_GRATIS);
        return diasCobrados * TARIFA_DIA;
    }

    @Override
    public String toString() {
        return super.toString() + " | Tipo: Estándar";
    }
}
