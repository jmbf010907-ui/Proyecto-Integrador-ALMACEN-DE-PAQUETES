import excepciones.AlmacenLlenoException;
import modelo.Anaquel;
import modelo.Destinatario;
import modelo.Envio;
import modelo.Paquete;
import modelo.PaqueteEstandar;
import modelo.PaqueteFragil;
import modelo.Remitente;
import servicio.GestorAlmacen;

import java.time.LocalDate;

/** Pruebas sin librerías externas. Cada verificación imprime [OK] o [FALLO]. */
public class PruebasAlmacen {
    private static int fallos = 0;
    private static final LocalDate HOY = LocalDate.of(2026, 10, 15);

    interface Accion { void ejecutar() throws Exception; }

    public static void main(String[] args) throws Exception {
        Remitente r = new Remitente("Tienda La Esquina", "5512345678");
        Destinatario d = new Destinatario("Ana López", "5587654321");

        System.out.println("--- P1 y P2: polimorfismo (referencia Paquete, dos objetos distintos) ---");
        Paquete estandar = new PaqueteEstandar("E-1", 2.5, r, d);
        Paquete fragil = new PaqueteFragil("F-1", 1.0, r, d, 20.0);
        verificar("P1 Estándar a 5 días cobra 2 días (3 gratis): 10.0", estandar.calcularCostoAlmacenaje(5) == 10.0);
        verificar("P1 Estándar a 2 días es gratis: 0.0", estandar.calcularCostoAlmacenaje(2) == 0.0);
        verificar("P2 Frágil a 5 días: 20 + 5*12 = 80.0", fragil.calcularCostoAlmacenaje(5) == 80.0);
        verificar("P2 Mismo método, resultado distinto por tipo de objeto",
                estandar.calcularCostoAlmacenaje(5) != fragil.calcularCostoAlmacenaje(5));
        verificar("Días negativos rechazados", lanza(IllegalArgumentException.class, () -> estandar.calcularCostoAlmacenaje(-1)));

        System.out.println("--- P3: formato incorrecto ---");
        verificar("P3 Texto 'abc' en un peso lanza NumberFormatException",
                lanza(NumberFormatException.class, () -> Double.parseDouble("abc")));

        System.out.println("--- P4: valores fuera de las reglas ---");
        verificar("P4 Peso 0 rechazado", lanza(IllegalArgumentException.class, () -> new PaqueteEstandar("X", 0, r, d)));
        verificar("P4 Peso negativo rechazado", lanza(IllegalArgumentException.class, () -> new PaqueteEstandar("X", -1, r, d)));
        verificar("P4 Peso NaN rechazado", lanza(IllegalArgumentException.class, () -> new PaqueteEstandar("X", Double.NaN, r, d)));
        verificar("P4 Código vacío rechazado", lanza(IllegalArgumentException.class, () -> new PaqueteEstandar(" ", 1, r, d)));
        verificar("P4 Remitente nulo rechazado", lanza(IllegalArgumentException.class, () -> new PaqueteEstandar("X", 1, null, d)));
        verificar("P4 Destinatario nulo rechazado", lanza(IllegalArgumentException.class, () -> new PaqueteEstandar("X", 1, r, null)));
        verificar("P4 Teléfono de 9 dígitos rechazado", lanza(IllegalArgumentException.class, () -> new Remitente("Ana", "123456789")));
        verificar("P4 Cargo de manejo negativo rechazado", lanza(IllegalArgumentException.class, () -> new PaqueteFragil("X", 1, r, d, -5)));

        System.out.println("--- Registro correcto ---");
        GestorAlmacen g = new GestorAlmacen(1);
        Envio e1 = g.registrarPaquete(new PaqueteEstandar("PQ-001", 2.5, r, d), HOY.minusDays(5));
        verificar("Registrar: el paquete existe", g.buscarPaquete("PQ-001") != null);
        verificar("Registrar: estado EN_ALMACEN", g.buscarPaquete("PQ-001").getEstado().equals(Paquete.EN_ALMACEN));
        verificar("Registrar: anaquel A-01 asignado", g.buscarPaquete("PQ-001").getAnaquel().getCodigo().equals("A-01"));
        verificar("Registrar: envío ACTIVO", e1.estaActivo());
        verificar("Pendientes: aparece con 3+ días", g.getPendientes(3, HOY).size() == 1);

        System.out.println("--- P5: excepción propia (almacén lleno) y estado conservado ---");
        int enviosAntes = g.getHistorial().size();
        verificar("P5 Estado antes: 1 envío y A-01 ocupado", enviosAntes == 1 && !g.getAnaqueles().get(0).estaLibre());
        verificar("P5 Segundo registro lanza AlmacenLlenoException",
                lanza(AlmacenLlenoException.class, () -> g.registrarPaquete(new PaqueteFragil("PQ-002", 1, r, d, 20), HOY)));
        verificar("P5 Estado después: sigue 1 envío", g.getHistorial().size() == enviosAntes);
        verificar("P5 Estado después: A-01 sigue con PQ-001",
                g.getAnaqueles().get(0).getPaquete().getCodigo().equals("PQ-001"));
        verificar("P5 El paquete rechazado no quedó registrado", g.buscarPaquete("PQ-002") == null);

        System.out.println("--- Rechazos que no deben dejar anaqueles ocupados ---");
        GestorAlmacen g2 = new GestorAlmacen(2);
        verificar("Fecha nula rechazada",
                lanza(IllegalArgumentException.class, () -> g2.registrarPaquete(new PaqueteEstandar("N-1", 1, r, d), null)));
        verificar("Fecha nula: ningún anaquel ocupado y ningún envío", todosLibres(g2) && g2.getHistorial().isEmpty());
        verificar("Paquete nulo rechazado", lanza(IllegalArgumentException.class, () -> g2.registrarPaquete(null, HOY)));
        verificar("Paquete nulo: ningún anaquel ocupado", todosLibres(g2));
        g2.registrarPaquete(new PaqueteEstandar("N-2", 1, r, d), HOY);
        verificar("Código duplicado rechazado",
                lanza(IllegalArgumentException.class, () -> g2.registrarPaquete(new PaqueteEstandar("N-2", 1, r, d), HOY)));
        verificar("Duplicado: solo un anaquel ocupado", ocupados(g2) == 1);

        System.out.println("--- Protección del estado del anaquel y del paquete ---");
        Anaquel libre = new Anaquel("Z-01");
        verificar("Liberar un anaquel libre lanza IllegalStateException", lanza(IllegalStateException.class, libre::liberar));
        verificar("Ocupar con paquete nulo lanza IllegalArgumentException", lanza(IllegalArgumentException.class, () -> libre.ocupar(null)));
        libre.ocupar(estandar);
        verificar("Ocupar un anaquel ocupado lanza IllegalStateException", lanza(IllegalStateException.class, () -> libre.ocupar(fragil)));
        verificar("setEstado(null) rechazado", lanza(IllegalArgumentException.class, () -> estandar.setEstado(null)));
        verificar("setEstado('XYZ') rechazado", lanza(IllegalArgumentException.class, () -> estandar.setEstado("XYZ")));

        System.out.println("--- Entregas rechazadas no cambian nada ---");
        verificar("Entrega con receptor vacío rechazada",
                lanza(IllegalArgumentException.class, () -> g.registrarEntrega("PQ-001", " ", HOY)));
        verificar("Entrega con fecha nula rechazada",
                lanza(IllegalArgumentException.class, () -> g.registrarEntrega("PQ-001", "Ana López", null)));
        verificar("Entrega con fecha anterior al ingreso rechazada",
                lanza(IllegalArgumentException.class, () -> g.registrarEntrega("PQ-001", "Ana López", HOY.minusDays(30))));
        verificar("Entrega de paquete inexistente rechazada",
                lanza(IllegalArgumentException.class, () -> g.registrarEntrega("NO-EXISTE", "Ana López", HOY)));
        verificar("Tras los rechazos: paquete sigue EN_ALMACEN, envío ACTIVO y anaquel ocupado",
                g.buscarPaquete("PQ-001").getEstado().equals(Paquete.EN_ALMACEN)
                        && e1.estaActivo() && !g.getAnaqueles().get(0).estaLibre());

        System.out.println("--- Costo por el gestor (mismo método, dos tipos) ---");
        verificar("Costo de PQ-001 (estándar, 5 días) = 10.0", g.calcularCostoAlmacenaje("PQ-001", HOY) == 10.0);

        System.out.println("--- Entrega válida ---");
        g.registrarEntrega("PQ-001", "Ana López", HOY);
        verificar("Entrega: paquete ENTREGADO", g.buscarPaquete("PQ-001").getEstado().equals(Paquete.ENTREGADO));
        verificar("Entrega: envío CERRADO", !e1.estaActivo());
        verificar("Entrega: anaquel liberado", todosLibres(g));
        verificar("Entrega: ya no es pendiente", g.getPendientes(3, HOY).isEmpty());
        verificar("Entrega doble rechazada",
                lanza(IllegalStateException.class, () -> g.registrarEntrega("PQ-001", "Ana López", HOY)));

        System.out.println("--- P6: nueva operación válida después de un fallo ---");
        Envio e3 = g.registrarPaquete(new PaqueteFragil("PQ-003", 1.0, r, d, 20), HOY);
        verificar("P6 Registro válido después del fallo: queda en A-01 y ACTIVO",
                e3.estaActivo() && e3.getPaquete().getAnaquel().getCodigo().equals("A-01"));

        System.out.println();
        if (fallos == 0) {
            System.out.println("Todas las pruebas pasaron.");
        } else {
            System.out.println("Fallaron " + fallos + " verificaciones.");
            System.exit(1);
        }
    }

    private static boolean todosLibres(GestorAlmacen g) { return ocupados(g) == 0; }

    private static int ocupados(GestorAlmacen g) {
        int n = 0;
        for (Anaquel a : g.getAnaqueles()) {
            if (!a.estaLibre()) n++;
        }
        return n;
    }

    /** True si la acción lanza esa excepción (o una subclase). */
    private static boolean lanza(Class<? extends Throwable> tipo, Accion accion) {
        try {
            accion.ejecutar();
            return false;
        } catch (Throwable t) {
            return tipo.isInstance(t);
        }
    }

    private static void verificar(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]    " : "[FALLO] ") + descripcion);
        if (!condicion) fallos++;
    }
}
