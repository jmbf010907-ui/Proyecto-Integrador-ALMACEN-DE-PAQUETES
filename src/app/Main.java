package app;

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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/** Menú de consola. Pide los datos y muestra los resultados; las reglas están en modelo y servicio. */
public class Main {

    public static void main(String[] args) {
        GestorAlmacen almacen = new GestorAlmacen(3);
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            if (!sc.hasNextLine()) {
                break;
            }
            String opcion = sc.nextLine().trim();
            switch (opcion) {
                case "1": registrar(sc, almacen, false); break;
                case "2": registrar(sc, almacen, true); break;
                case "3": buscar(sc, almacen); break;
                case "4": entregar(sc, almacen); break;
                case "5": costo(sc, almacen); break;
                case "6": pendientes(sc, almacen); break;
                case "7": verHistorialYAnaqueles(almacen); break;
                case "8": demostrarPolimorfismo(); break;
                case "0": salir = true; break;
                default: System.out.println("Opción no válida. Elige un número del menú.");
            }
        }
        System.out.println("Hasta luego.");
    }

    private static void mostrarMenu() {
        System.out.println("\n===== ALMACÉN DE PAQUETES =====");
        System.out.println("1. Registrar paquete estándar");
        System.out.println("2. Registrar paquete frágil");
        System.out.println("3. Buscar paquete por código");
        System.out.println("4. Registrar entrega");
        System.out.println("5. Calcular costo de almacenaje");
        System.out.println("6. Ver paquetes pendientes");
        System.out.println("7. Ver historial y anaqueles");
        System.out.println("8. Demostración de polimorfismo");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
    }

    // Funcionalidad 1: registrar paquete y asignar anaquel
    private static void registrar(Scanner sc, GestorAlmacen almacen, boolean fragil) {
        try {
            String codigo = leer(sc, "Código del paquete: ");
            double peso = Double.parseDouble(leer(sc, "Peso en kg (ej. 2.5): "));
            Remitente r = new Remitente(leer(sc, "Nombre del remitente: "),
                                        leer(sc, "Teléfono del remitente (10 dígitos): "));
            Destinatario d = new Destinatario(leer(sc, "Nombre del destinatario: "),
                                              leer(sc, "Teléfono del destinatario (10 dígitos): "));
            Paquete p;
            if (fragil) {
                double manejo = Double.parseDouble(leer(sc, "Cargo de manejo en pesos (ej. 20): "));
                p = new PaqueteFragil(codigo, peso, r, d, manejo);
            } else {
                p = new PaqueteEstandar(codigo, peso, r, d);
            }
            LocalDate fecha = leerFecha(sc);

            Envio envio = almacen.registrarPaquete(p, fecha);
            System.out.println("Registrado con éxito: " + envio); // solo si no hubo error
            System.out.println(p);
        } catch (NumberFormatException ex) {
            // va antes que IllegalArgumentException porque es una subclase de ella
            System.out.println("Formato incorrecto: escribe un número, por ejemplo 2.5 (sin letras).");
        } catch (DateTimeParseException ex) {
            System.out.println("Fecha incorrecta: usa el formato AAAA-MM-DD, por ejemplo 2026-11-15.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Dato inválido: " + ex.getMessage());
        } catch (AlmacenLlenoException ex) {
            System.out.println("No se pudo registrar: " + ex.getMessage());
        } finally {
            System.out.println("Intento finalizado."); // se muestra con éxito o con error
        }
    }

    // Funcionalidad 3: buscar paquete
    private static void buscar(Scanner sc, GestorAlmacen almacen) {
        String codigo = leer(sc, "Código a buscar: ");
        Paquete p = almacen.buscarPaquete(codigo);
        System.out.println(p == null ? "No se encontró el paquete " + codigo : p);
    }

    // Funcionalidad 2: registrar entrega
    private static void entregar(Scanner sc, GestorAlmacen almacen) {
        try {
            String codigo = leer(sc, "Código del paquete: ");
            String recibe = leer(sc, "Nombre de quien recibe: ");
            LocalDate fecha = leerFecha(sc);

            almacen.registrarEntrega(codigo, recibe, fecha);
            System.out.println("Entrega registrada con éxito. " + almacen.buscarPaquete(codigo));
        } catch (DateTimeParseException ex) {
            System.out.println("Fecha incorrecta: usa el formato AAAA-MM-DD, por ejemplo 2026-11-15.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("No se pudo registrar la entrega: " + ex.getMessage());
        } finally {
            System.out.println("Intento finalizado.");
        }
    }

    // Funcionalidad 4: costo de almacenaje (polimorfismo)
    private static void costo(Scanner sc, GestorAlmacen almacen) {
        try {
            String codigo = leer(sc, "Código del paquete: ");
            LocalDate fecha = leerFecha(sc);
            Paquete p = almacen.buscarPaquete(codigo);
            double total = almacen.calcularCostoAlmacenaje(codigo, fecha);
            System.out.println("Tipo de objeto: " + p.getClass().getSimpleName());
            System.out.printf("Costo de almacenaje: $%.2f%n", total);
        } catch (DateTimeParseException ex) {
            System.out.println("Fecha incorrecta: usa el formato AAAA-MM-DD, por ejemplo 2026-11-15.");
        } catch (IllegalArgumentException ex) {
            System.out.println("No se pudo calcular: " + ex.getMessage());
        }
    }

    // Funcionalidad 5: paquetes pendientes
    private static void pendientes(Scanner sc, GestorAlmacen almacen) {
        try {
            int dias = Integer.parseInt(leer(sc, "Mínimo de días guardado (ej. 3): "));
            LocalDate fecha = leerFecha(sc);
            List<Envio> lista = almacen.getPendientes(dias, fecha);
            if (lista.isEmpty()) {
                System.out.println("No hay paquetes pendientes con ese criterio.");
            }
            lista.forEach(System.out::println);
        } catch (NumberFormatException ex) {
            System.out.println("Formato incorrecto: escribe un número entero, por ejemplo 3.");
        } catch (DateTimeParseException ex) {
            System.out.println("Fecha incorrecta: usa el formato AAAA-MM-DD, por ejemplo 2026-11-15.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Dato inválido: " + ex.getMessage());
        }
    }

    // Funcionalidad 6: historial y estado de anaqueles
    private static void verHistorialYAnaqueles(GestorAlmacen almacen) {
        System.out.println("Historial:");
        List<Envio> historial = almacen.getHistorial();
        if (historial.isEmpty()) {
            System.out.println("(sin envíos)");
        }
        historial.forEach(System.out::println);
        System.out.println("Anaqueles:");
        for (Anaquel a : almacen.getAnaqueles()) {
            System.out.println(a);
        }
    }

    // Polimorfismo: referencias de tipo Paquete con objetos de cada subclase
    private static void demostrarPolimorfismo() {
        Remitente r = new Remitente("Demo Remitente", "5500000001");
        Destinatario d = new Destinatario("Demo Destinatario", "5500000002");
        Paquete[] paquetes = {
            new PaqueteEstandar("DEMO-E", 1.0, r, d),
            new PaqueteFragil("DEMO-F", 1.0, r, d, 20.0)
        };
        int dias = 5;
        System.out.println("Costo de almacenaje a " + dias + " días, llamando con referencias de tipo Paquete:");
        for (Paquete p : paquetes) {
            System.out.printf("Referencia: Paquete | Objeto: %s | Costo: $%.2f%n",
                    p.getClass().getSimpleName(), p.calcularCostoAlmacenaje(dias));
        }
    }

    private static String leer(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    private static LocalDate leerFecha(Scanner sc) {
        String texto = leer(sc, "Fecha AAAA-MM-DD (Enter = hoy): ");
        return texto.isEmpty() ? LocalDate.now() : LocalDate.parse(texto);
    }
}
