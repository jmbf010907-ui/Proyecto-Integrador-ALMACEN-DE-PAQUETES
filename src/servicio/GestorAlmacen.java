package servicio;

import excepciones.AlmacenLlenoException;
import modelo.Anaquel;
import modelo.Entrega;
import modelo.Envio;
import modelo.Paquete;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Reglas del almacén: registrar, buscar, entregar, cotizar y consultar pendientes. */
public class GestorAlmacen {
    private List<Anaquel> anaqueles = new ArrayList<>();
    private List<Envio> envios = new ArrayList<>();

    public GestorAlmacen(int numeroAnaqueles) {
        if (numeroAnaqueles <= 0) {
            throw new IllegalArgumentException("Debe haber al menos un anaquel.");
        }
        for (int i = 1; i <= numeroAnaqueles; i++) {
            anaqueles.add(new Anaquel(String.format("A-%02d", i)));
        }
    }

    /**
     * Funcionalidad 1. Registra un paquete (estándar o frágil) y le asigna un anaquel libre.
     * Orden: 1) validar todos los datos, 2) buscar anaquel, 3) crear el Envio,
     * 4) solo al final ocupar el anaquel y guardar el envío. Un rechazo no deja cambios.
     */
    public Envio registrarPaquete(Paquete p, LocalDate fecha) throws AlmacenLlenoException {
        if (p == null) {
            throw new IllegalArgumentException("Se requiere un paquete.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
        }
        if (buscarEnvio(p.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un paquete con el código " + p.getCodigo());
        }
        if (p.getAnaquel() != null || !p.estaEnAlmacen()) {
            throw new IllegalArgumentException("El paquete " + p.getCodigo() + " ya fue ubicado o entregado.");
        }
        Anaquel libre = buscarAnaquelLibre();
        if (libre == null) {
            throw new AlmacenLlenoException("No hay anaqueles libres; el paquete " + p.getCodigo() + " no se registró.");
        }
        Envio e = new Envio(p, fecha);   // valida sus datos sin tocar el almacén

        libre.ocupar(p);                 // cambios de estado, solo al final
        envios.add(e);
        return e;
    }

    /** Devuelve el paquete con ese código, o null si no existe. */
    public Paquete buscarPaquete(String codigo) {
        Envio e = buscarEnvio(codigo);
        return (e == null) ? null : e.getPaquete();
    }

    /** Devuelve el envío con ese código, o null si no existe. */
    public Envio buscarEnvio(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (Envio e : envios) {
            if (e.getPaquete().getCodigo().equalsIgnoreCase(codigo.trim())) {
                return e;
            }
        }
        return null;
    }

    /**
     * Funcionalidad 2. Registra la entrega. Envio.cerrar valida y después cambia, en un solo
     * paso, el envío (CERRADO), el paquete (ENTREGADO) y el anaquel (libre).
     */
    public void registrarEntrega(String codigo, String recibidoPor, LocalDate fecha) {
        Envio envio = buscarEnvio(codigo);
        if (envio == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigo);
        }
        Entrega entrega = new Entrega(fecha, recibidoPor); // valida fecha y receptor
        envio.cerrar(entrega);
    }

    /**
     * Días cobrables: desde el ingreso hasta la fecha de consulta
     * (o hasta la salida, si el envío ya se cerró). Solo consulta; no modifica nada.
     */
    public int calcularDiasAlmacenado(String codigo, LocalDate hoy) {
        if (hoy == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        Envio envio = buscarEnvio(codigo);
        if (envio == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigo);
        }
        LocalDate fin = envio.estaActivo() ? hoy : envio.getFechaSalida();
        long dias = ChronoUnit.DAYS.between(envio.getFechaIngreso(), fin);
        if (dias < 0) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a la fecha de ingreso.");
        }
        return (int) dias;
    }

    /**
     * Funcionalidad 3. Cotiza el costo de almacenaje. Usa una referencia Paquete:
     * cada subclase calcula a su manera (polimorfismo). No cambia el envío ni el anaquel.
     */
    public double calcularCostoAlmacenaje(String codigo, LocalDate hoy) {
        int dias = calcularDiasAlmacenado(codigo, hoy);
        Paquete p = buscarPaquete(codigo);
        return p.calcularCostoAlmacenaje(dias);
    }

    public List<Envio> getHistorial() { return new ArrayList<>(envios); }

    /**
     * Funcionalidad 4. Envíos activos con N o más días guardados.
     * Sirve para avisar al remitente que su paquete no ha sido recogido.
     */
    public List<Envio> getPendientes(int diasMinimos, LocalDate hoy) {
        if (hoy == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        if (diasMinimos < 0) {
            throw new IllegalArgumentException("Los días mínimos no pueden ser negativos.");
        }
        List<Envio> pendientes = new ArrayList<>();
        for (Envio e : envios) {
            if (e.estaActivo() && ChronoUnit.DAYS.between(e.getFechaIngreso(), hoy) >= diasMinimos) {
                pendientes.add(e);
            }
        }
        return pendientes;
    }

    public List<Anaquel> getAnaqueles() { return new ArrayList<>(anaqueles); }

    private Anaquel buscarAnaquelLibre() {
        for (Anaquel a : anaqueles) {
            if (a.estaLibre()) {
                return a;
            }
        }
        return null;
    }
}
