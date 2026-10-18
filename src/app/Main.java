package app;

import modelo.Anaquel;
import modelo.Destinatario;
import modelo.Envio;
import modelo.Paquete;
import modelo.Remitente;
import servicio.GestorAlmacen;

import java.time.LocalDate;

/** Demostración de dos funcionalidades: registrar/ubicar paquete y registrar entrega. */
public class Main {
    public static void main(String[] args) {
        GestorAlmacen almacen = new GestorAlmacen(3);
        LocalDate hoy = LocalDate.of(2026, 10, 15);

        System.out.println("=== PRUEBA 1: Registrar paquete y asignar anaquel ===");
        Remitente r = new Remitente("Tienda La Esquina", "5512345678");
        Destinatario d = new Destinatario("Ana López", "5587654321");
        Envio envio = almacen.registrarPaquete("PQ-001", 2.5, r, d, hoy.minusDays(5));
        System.out.println("Registrado: " + envio);
        System.out.println(almacen.buscarPaquete("PQ-001"));

        System.out.println("\nCaso de error (peso inválido):");
        try {
            almacen.registrarPaquete("PQ-002", -1, r, d, hoy);
        } catch (IllegalArgumentException ex) {
            System.out.println("Error controlado: " + ex.getMessage());
        }

        System.out.println("\nPendientes con 3+ días guardados:");
        almacen.getPendientes(3, hoy).forEach(System.out::println);

        System.out.println("\n=== PRUEBA 2: Registrar entrega ===");
        almacen.registrarEntrega("PQ-001", "Ana López", hoy);
        Paquete p = almacen.buscarPaquete("PQ-001");
        System.out.println(p);
        System.out.println("Historial:");
        almacen.getHistorial().forEach(System.out::println);

        System.out.println("\nEstado de anaqueles:");
        for (Anaquel a : almacen.getAnaqueles()) {
            System.out.println(a);
        }
    }
}
