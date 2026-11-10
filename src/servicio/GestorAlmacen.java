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

/** Reglas del almacén: registrar, buscar, entregar y calcular costos. */
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
     * Registra un paquete (estándar o frágil) y le asigna un anaquel libre.
     * Primero valida todo; el anaquel se ocupa al final, así un rechazo no deja cambios.
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
        Anaquel libre = buscarAnaquelLibre();
        if (libre == null) {
            throw new AlmacenLlenoException("No hay anaqueles libres; el paquete " + p.getCodigo() + " no se registró.");
        }
        Envio e = new Envio(p, fecha);

        libre.ocupar(p);
        p.setAnaquel(libre);
        envios.add(e);
        return e;
    }

    /** Devuelve el paquete con ese código, o null si no existe. */
    public Paquete buscarPaquete(String codigo) {
        Envio e = buscarEnvio(codigo);
        return (e == null) ? null : e.getPaquete();
    }

    /** Registra la entrega, cambia los estados y libera el anaquel. */
    public void registrarEntrega(String codigo, String recibidoPor, LocalDate fecha) {
        Envio envio = buscarEnvio(codigo);
        if (envio == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigo);
        }
        Entrega entrega = new Entrega(fecha, recibidoPor);
        envio.cerrar(entrega);
        Paquete p = envio.getPaquete();
        p.setEstado(Paquete.ENTREGADO);
        p.getAnaquel().liberar();
        p.setAnaquel(null);
    }

    /** Costo de almacenaje. Usa una referencia Paquete: cada tipo calcula a su manera (polimorfismo). */
    public double calcularCostoAlmacenaje(String codigo, LocalDate hoy) {
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
        Paquete p = envio.getPaquete();
        return p.calcularCostoAlmacenaje((int) dias);
    }

    public List<Envio> getHistorial() { return new ArrayList<>(envios); }

    /** Envíos activos con N o más días guardados. */
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

    private Envio buscarEnvio(String codigo) {
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

    private Anaquel buscarAnaquelLibre() {
        for (Anaquel a : anaqueles) {
            if (a.estaLibre()) {
                return a;
            }
        }
        return null;
    }
}
