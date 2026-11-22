import app.Main;
import excepciones.AlmacenLlenoException;
import modelo.Anaquel;
import modelo.Destinatario;
import modelo.Entrega;
import modelo.Envio;
import modelo.Paquete;
import modelo.PaqueteEstandar;
import modelo.PaqueteFragil;
import modelo.Remitente;
import servicio.GestorAlmacen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/** Pruebas sin librerías externas. Cada verificación imprime [OK] o [FALLO]. */
public class PruebasAlmacen {
    private static int fallos = 0;
    private static int total = 0;
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

        System.out.println("--- P1 frontera de días gratis (estándar): 3 días = $0, 4 días = $5 ---");
        GestorAlmacen gf = new GestorAlmacen(1);
        LocalDate ingreso = LocalDate.of(2026, 11, 10);
        Envio ef = gf.registrarPaquete(new PaqueteEstandar("B-1", 1.0, r, d), ingreso);
        LocalDate consulta3 = ingreso.plusDays(3);   // 2026-11-13
        LocalDate consulta4 = ingreso.plusDays(4);   // 2026-11-14
        System.out.println("        Ingreso " + ingreso + " | consultas " + consulta3 + " y " + consulta4);
        verificar("Frontera: consulta " + consulta3 + " = 3 días", gf.calcularDiasAlmacenado("B-1", consulta3) == 3);
        verificar("Frontera: a 3 días el estándar cuesta $0.00", gf.calcularCostoAlmacenaje("B-1", consulta3) == 0.0);
        verificar("Frontera: consulta " + consulta4 + " = 4 días", gf.calcularDiasAlmacenado("B-1", consulta4) == 4);
        verificar("Frontera: a 4 días el estándar cuesta $5.00", gf.calcularCostoAlmacenaje("B-1", consulta4) == 5.0);
        verificar("Cotizar no modifica el envío (sigue ACTIVO, sin salida ni entrega)",
                ef.estaActivo() && ef.getFechaSalida() == null && ef.getEntrega() == null);
        verificar("Cotizar no modifica el anaquel (A-01 sigue con B-1) ni el paquete (EN_ALMACEN)",
                gf.getAnaqueles().get(0).getPaquete() == ef.getPaquete()
                        && ef.getPaquete().getAnaquel() == gf.getAnaqueles().get(0)
                        && ef.getPaquete().estaEnAlmacen());

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
        Paquete rechazado = new PaqueteFragil("PQ-002", 1, r, d, 20);
        verificar("P5 Segundo registro lanza AlmacenLlenoException",
                lanza(AlmacenLlenoException.class, () -> g.registrarPaquete(rechazado, HOY)));
        verificar("P5 Estado después: sigue 1 envío", g.getHistorial().size() == enviosAntes);
        verificar("P5 Estado después: A-01 sigue con PQ-001",
                g.getAnaqueles().get(0).getPaquete().getCodigo().equals("PQ-001"));
        verificar("P5 El paquete rechazado no quedó registrado ni ubicado",
                g.buscarPaquete("PQ-002") == null && rechazado.getAnaquel() == null);

        System.out.println("--- Prueba sugerida (2.º avance): fecha nula y luego registro válido en el mismo anaquel ---");
        GestorAlmacen g1 = new GestorAlmacen(1);
        verificar("Fecha nula rechazada con IllegalArgumentException",
                lanza(IllegalArgumentException.class, () -> g1.registrarPaquete(new PaqueteEstandar("N-0", 1, r, d), null)));
        verificar("Tras la fecha nula: A-01 libre e historial vacío",
                g1.getAnaqueles().get(0).estaLibre() && g1.getHistorial().isEmpty());
        verificar("Registro válido en el mismo anaquel funciona",
                !lanza(Exception.class, () -> g1.registrarPaquete(new PaqueteEstandar("N-0", 1, r, d), HOY))
                        && g1.buscarPaquete("N-0").getAnaquel().getCodigo().equals("A-01"));

        System.out.println("--- Rechazos que no deben dejar anaqueles ocupados ---");
        GestorAlmacen g2 = new GestorAlmacen(2);
        verificar("Paquete nulo rechazado", lanza(IllegalArgumentException.class, () -> g2.registrarPaquete(null, HOY)));
        verificar("Paquete nulo: ningún anaquel ocupado", todosLibres(g2));
        g2.registrarPaquete(new PaqueteEstandar("N-2", 1, r, d), HOY);
        verificar("Código duplicado rechazado",
                lanza(IllegalArgumentException.class, () -> g2.registrarPaquete(new PaqueteEstandar("N-2", 1, r, d), HOY)));
        verificar("Duplicado: solo un anaquel ocupado", ocupados(g2) == 1);

        System.out.println("--- Protección del estado del paquete y del anaquel ---");
        verificar("Paquete no tiene setEstado ni setAnaquel públicos",
                !tieneMetodoPublico(Paquete.class, "setEstado") && !tieneMetodoPublico(Paquete.class, "setAnaquel"));
        Anaquel z1 = new Anaquel("Z-01");
        Anaquel z2 = new Anaquel("Z-02");
        verificar("Liberar un anaquel libre lanza IllegalStateException", lanza(IllegalStateException.class, z1::liberar));
        verificar("Ocupar con paquete nulo lanza IllegalArgumentException", lanza(IllegalArgumentException.class, () -> z1.ocupar(null)));
        z1.ocupar(estandar);
        verificar("Ocupar enlaza en ambos sentidos (anaquel → paquete y paquete → anaquel)",
                z1.getPaquete() == estandar && estandar.getAnaquel() == z1);
        verificar("Ocupar un anaquel ocupado lanza IllegalStateException", lanza(IllegalStateException.class, () -> z1.ocupar(fragil)));
        verificar("Un paquete ya ubicado no se puede poner en otro anaquel",
                lanza(IllegalStateException.class, () -> z2.ocupar(estandar)));
        verificar("Tras el rechazo: Z-02 libre y el paquete sigue en Z-01", z2.estaLibre() && estandar.getAnaquel() == z1);
        z1.liberar();
        verificar("Liberar desenlaza en ambos sentidos", z1.estaLibre() && estandar.getAnaquel() == null);
        Envio sinAnaquel = new Envio(fragil, HOY);
        verificar("Cerrar un envío cuyo paquete no está en un anaquel se rechaza",
                lanza(IllegalStateException.class, () -> sinAnaquel.cerrar(new Entrega(HOY, "Ana López"))));
        verificar("Tras el rechazo: envío ACTIVO y paquete EN_ALMACEN", sinAnaquel.estaActivo() && fragil.estaEnAlmacen());

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

        System.out.println("--- Costo por el gestor ---");
        verificar("Costo de PQ-001 (estándar, 5 días) = 10.0", g.calcularCostoAlmacenaje("PQ-001", HOY) == 10.0);

        System.out.println("--- Entrega válida ---");
        Paquete pq1 = g.buscarPaquete("PQ-001");
        g.registrarEntrega("PQ-001", "Ana López", HOY);
        verificar("Entrega: paquete ENTREGADO", pq1.getEstado().equals(Paquete.ENTREGADO));
        verificar("Entrega: envío CERRADO", !e1.estaActivo());
        verificar("Entrega: anaquel liberado y paquete sin anaquel", todosLibres(g) && pq1.getAnaquel() == null);
        verificar("Entrega: ya no es pendiente", g.getPendientes(3, HOY).isEmpty());
        verificar("Entrega doble rechazada",
                lanza(IllegalStateException.class, () -> g.registrarEntrega("PQ-001", "Ana López", HOY)));
        verificar("Un paquete entregado no puede volver a ocupar un anaquel",
                lanza(IllegalStateException.class, () -> new Anaquel("Z-03").ocupar(pq1)));

        System.out.println("--- P6: nueva operación válida después de un fallo ---");
        Envio e3 = g.registrarPaquete(new PaqueteFragil("PQ-003", 1.0, r, d, 20), HOY);
        verificar("P6 Registro válido después del fallo: queda en A-01 y ACTIVO",
                e3.estaActivo() && e3.getPaquete().getAnaquel().getCodigo().equals("A-01"));

        System.out.println("--- P7: finally en un intento fallido y en uno válido (menú real) ---");
        String salida7 = ejecutarMenu(
                "1", "PQ-001", "abc",                                              // fallido
                "1", "PQ-001", "2.5", "Tienda La Esquina", "5512345678",
                "Ana López", "5587654321", "2026-10-10",                           // válido
                "0");
        verificar("P7 «Intento finalizado.» aparece 2 veces", contar(salida7, "Intento finalizado.") == 2);
        verificar("P7 «Registrado con éxito» aparece 1 vez", contar(salida7, "Registrado con éxito") == 1);
        verificar("P7 El intento fallido muestra el mensaje de formato", salida7.contains("Formato incorrecto"));

        System.out.println("--- P8: flujo completo con las cuatro funcionalidades (menú real) ---");
        String salida8 = ejecutarMenu(
                "1", "PQ-101", "2", "Papelería Sol", "5533333333", "Luis Soto", "5544444444", "2026-11-01",
                "2", "PQ-102", "1.5", "Juan Pérez", "5511111111", "María Ruiz", "5522222222", "20", "2026-11-03",
                "6", "3", "2026-11-05",          // pendientes: solo PQ-101 (4 días)
                "5", "PQ-101", "2026-11-05",     // costo estándar a 4 días: $5.00
                "5", "PQ-102", "2026-11-05",     // costo frágil a 2 días: 20 + 24 = $44.00
                "4", "PQ-101", "Luis Soto", "2026-11-05",   // entrega: libera A-01
                "6", "3", "2026-11-05",          // ya no hay pendientes
                "1", "PQ-103", "3", "Papelería Sol", "5533333333", "Rosa Díaz", "5566666666", "2026-11-05",
                "7",
                "0");
        verificar("P8 Registro estándar en A-01 y frágil en A-02",
                salida8.contains("PQ-101 | 2.0 kg | EN_ALMACEN | A-01") && salida8.contains("PQ-102 | 1.5 kg | EN_ALMACEN | A-02"));
        verificar("P8 Pendientes muestra PQ-101 y al remitente a quien avisar",
                salida8.contains("Envío [PQ-101] ingreso 2026-11-01 | ACTIVO | A-01 | Avisar al remitente: Papelería Sol (5533333333)"));
        verificar("P8 Costo estándar a 4 días = $5.00 y frágil a 2 días = $44.00",
                salida8.contains("Costo de almacenaje: $5.00") && salida8.contains("Costo de almacenaje: $44.00"));
        verificar("P8 La entrega cierra el envío y libera el anaquel",
                salida8.contains("Entrega registrada con éxito. Paquete PQ-101 | 2.0 kg | ENTREGADO | sin anaquel"));
        verificar("P8 Después de la entrega no hay pendientes",
                salida8.contains("No hay paquetes pendientes con ese criterio."));
        verificar("P8 El nuevo paquete reutiliza el anaquel liberado (A-01)",
                salida8.contains("PQ-103 | 3.0 kg | EN_ALMACEN | A-01"));
        verificar("P8 Historial final coherente",
                salida8.contains("Envío [PQ-101] ingreso 2026-11-01 | CERRADO | Entrega el 2026-11-05 a Luis Soto")
                        && salida8.contains("A-01 [ocupado por PQ-103]") && salida8.contains("A-02 [ocupado por PQ-102]")
                        && salida8.contains("A-03 [libre]"));
        verificar("P8 El programa termina con la opción 0", salida8.contains("Hasta luego."));

        System.out.println();
        if (fallos == 0) {
            System.out.println("Todas las pruebas pasaron (" + total + " verificaciones).");
        } else {
            System.out.println("Fallaron " + fallos + " de " + total + " verificaciones.");
            System.exit(1);
        }
    }

    /** Ejecuta app.Main con las líneas dadas como entrada de teclado y devuelve lo que imprimió. */
    private static String ejecutarMenu(String... lineas) {
        InputStream inOriginal = System.in;
        PrintStream outOriginal = System.out;
        ByteArrayOutputStream capturado = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream((String.join("\n", lineas) + "\n").getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturado, true, StandardCharsets.UTF_8));
            Main.main(new String[0]);
        } finally {
            System.setIn(inOriginal);
            System.setOut(outOriginal);
        }
        return capturado.toString(StandardCharsets.UTF_8);
    }

    private static int contar(String texto, String buscado) {
        int n = 0;
        for (int i = texto.indexOf(buscado); i >= 0; i = texto.indexOf(buscado, i + 1)) n++;
        return n;
    }

    private static boolean tieneMetodoPublico(Class<?> clase, String nombre) {
        for (Method m : clase.getMethods()) {
            if (m.getName().equals(nombre) && Modifier.isPublic(m.getModifiers())) return true;
        }
        return false;
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
        total++;
        System.out.println((condicion ? "[OK]    " : "[FALLO] ") + descripcion);
        if (!condicion) fallos++;
    }
}
