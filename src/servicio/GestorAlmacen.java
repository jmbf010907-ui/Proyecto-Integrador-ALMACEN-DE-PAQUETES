package servicio;

import modelo.Anaquel;
import modelo.Destinatario;
import modelo.Entrega;
import modelo.Envio;
import modelo.Paquete;
import modelo.Remitente;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Lógica del almacén: registro, ubicación, búsqueda y entrega de paquetes. */
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

    /** Funcionalidad 1 y 2: registra el paquete, crea su envío y le asigna un anaquel libre. */
    public Envio registrarPaquete(String codigo, double peso, Remitente r, Destinatario d, LocalDate fecha) {
        if (buscarPaquete(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un paquete con el código " + codigo);
        }
        Anaquel libre = buscarAnaquelLibre();
        if (libre == null) {
            throw new IllegalStateException("No hay anaqueles libres.");
        }
        Paquete p = new Paquete(codigo, peso, r, d);
        libre.ocupar(p);
        p.setAnaquel(libre);
        Envio e = new Envio(p, fecha);
        envios.add(e);
        return e;
    }

    /** Funcionalidad 3: busca un paquete por código (null si no existe). */
    public Paquete buscarPaquete(String codigo) {
        for (Envio e : envios) {
            if (e.getPaquete().getCodigo().equalsIgnoreCase(codigo)) {
                return e.getPaquete();
            }
        }
        return null;
    }

    /** Funcionalidades 4, 5 y 7: entrega, cambia estados y libera el anaquel. */
    public void registrarEntrega(String codigo, String recibidoPor, LocalDate fecha) {
        Envio envio = buscarEnvio(codigo);
        if (envio == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigo);
        }
        envio.cerrar(new Entrega(fecha, recibidoPor));
        Paquete p = envio.getPaquete();
        p.setEstado(Paquete.ENTREGADO);
        p.getAnaquel().liberar();
        p.setAnaquel(null);
    }

    /** Funcionalidad 6: historial completo de envíos. */
    public List<Envio> getHistorial() { return new ArrayList<>(envios); }

    /** Funcionalidad 8: envíos activos con más de N días guardados. */
    public List<Envio> getPendientes(int diasMinimos, LocalDate hoy) {
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
        for (Envio e : envios) {
            if (e.getPaquete().getCodigo().equalsIgnoreCase(codigo)) {
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
