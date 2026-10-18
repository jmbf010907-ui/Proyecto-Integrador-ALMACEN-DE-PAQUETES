import modelo.Destinatario;
import modelo.Envio;
import modelo.Paquete;
import modelo.Remitente;
import servicio.GestorAlmacen;

import java.time.LocalDate;

/** Pruebas sencillas sin librerías externas. Si alguna falla, el programa termina con error. */
public class PruebasAlmacen {
    private static int fallos = 0;

    private static void verificar(String nombre, boolean condicion) {
        System.out.println((condicion ? "[OK]    " : "[FALLO] ") + nombre);
        if (!condicion) {
            fallos++;
        }
    }

    public static void main(String[] args) {
        Remitente r = new Remitente("Tienda La Esquina", "5512345678");
        Destinatario d = new Destinatario("Ana López", "5587654321");
        LocalDate hoy = LocalDate.of(2026, 10, 15);

        // Prueba 1: registrar paquete y asignar anaquel
        GestorAlmacen g = new GestorAlmacen(2);
        Envio e = g.registrarPaquete("PQ-001", 2.5, r, d, hoy.minusDays(5));
        Paquete p = g.buscarPaquete("PQ-001");
        verificar("Registrar: el paquete existe", p != null);
        verificar("Registrar: estado EN_ALMACEN", Paquete.EN_ALMACEN.equals(p.getEstado()));
        verificar("Registrar: anaquel A-01 asignado", "A-01".equals(p.getAnaquel().getCodigo()));
        verificar("Registrar: envío ACTIVO", e.estaActivo());

        // Validaciones
        boolean pesoInvalido = false;
        try {
            g.registrarPaquete("PQ-002", -1, r, d, hoy);
        } catch (IllegalArgumentException ex) {
            pesoInvalido = true;
        }
        verificar("Validación: peso negativo rechazado", pesoInvalido);

        boolean duplicado = false;
        try {
            g.registrarPaquete("PQ-001", 1, r, d, hoy);
        } catch (IllegalArgumentException ex) {
            duplicado = true;
        }
        verificar("Validación: código duplicado rechazado", duplicado);

        // Pendientes
        verificar("Pendientes: aparece con 3+ días", g.getPendientes(3, hoy).size() == 1);

        // Prueba 2: entrega
        g.registrarEntrega("PQ-001", "Ana López", hoy);
        verificar("Entrega: paquete ENTREGADO", Paquete.ENTREGADO.equals(p.getEstado()));
        verificar("Entrega: envío CERRADO", !e.estaActivo());
        verificar("Entrega: anaquel liberado", g.getAnaqueles().get(0).estaLibre());
        verificar("Entrega: ya no es pendiente", g.getPendientes(0, hoy).isEmpty());

        // Almacén lleno
        GestorAlmacen pequeno = new GestorAlmacen(1);
        pequeno.registrarPaquete("X-1", 1, r, d, hoy);
        boolean lleno = false;
        try {
            pequeno.registrarPaquete("X-2", 1, r, d, hoy);
        } catch (IllegalStateException ex) {
            lleno = true;
        }
        verificar("Almacén lleno: se rechaza el registro", lleno);

        System.out.println(fallos == 0 ? "\nTodas las pruebas pasaron." : "\nPruebas fallidas: " + fallos);
        if (fallos > 0) {
            System.exit(1);
        }
    }
}
