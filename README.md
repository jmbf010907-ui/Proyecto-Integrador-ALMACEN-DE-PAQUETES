# Almacén de Paquetes

Proyecto Integrador de Programación Orientada a Objetos (Java) — Facultad de Ingeniería, Grupo 4.

Sistema de gestión de un almacén de paquetes: registro, asignación de anaquel, búsqueda, entrega, historial, pendientes y costo de almacenaje.
**Versión: tercer avance** (herencia, polimorfismo y manejo de excepciones).

## Estructura
```
.github/workflows/   CI: compila, corre pruebas y Main en cada push
src/
├── modelo/          Paquete (base), PaqueteEstandar, PaqueteFragil,
│                    Remitente, Destinatario, Anaquel, Envio, Entrega
├── servicio/        GestorAlmacen
├── excepciones/     AlmacenLlenoException (excepción propia, hereda de Exception)
└── app/             Main (menú de consola)
tests/               PruebasAlmacen.java
docs/                UML (dot/png/svg), reporte del tercer avance y evidencias
INSTRUCTIONS.md      Instrucciones del avance y reparto del equipo
RUBRIC.md            Rúbrica de evaluación
```

## Requisitos
JDK 17 o superior (probado con Java 21).

## Compilar y ejecutar
Desde la carpeta raíz del proyecto (la que contiene `src` y `tests`):
```
javac -encoding UTF-8 -d out src/modelo/*.java src/servicio/*.java src/excepciones/*.java src/app/*.java tests/*.java
java -cp out app.Main
java -cp out PruebasAlmacen
```

## Funcionalidades (menú de `app.Main`)
1. Registrar paquete estándar con remitente y destinatario (asigna anaquel libre).
2. Registrar paquete frágil con remitente y destinatario (asigna anaquel libre).
3. Buscar paquete por código.
4. Registrar entrega (cierra el envío y libera el anaquel).
5. Calcular costo de almacenaje (método polimórfico).
6. Ver paquetes pendientes (N o más días guardados).
7. Ver historial de envíos y estado de anaqueles.
8. Demostración de polimorfismo.
0. Salir.

La fecha se escribe como `AAAA-MM-DD`; con Enter se usa la fecha de hoy. El almacén tiene 3 anaqueles y los datos se guardan en memoria.

## Herencia y polimorfismo
`PaqueteEstandar` y `PaqueteFragil` son un `Paquete`. El método `calcularCostoAlmacenaje(int dias)` se define en `Paquete` y se sobrescribe en ambas subclases:
- Estándar: 3 días gratis y 5 pesos por día después.
- Frágil: cargo de manejo (por ejemplo 20 pesos) más 12 pesos por día desde el primer día.

## Excepciones
- `NumberFormatException`: texto donde se espera un número (peso, cargo de manejo, días).
- `IllegalArgumentException`: peso ≤ 0, nombre o código vacío, teléfono inválido, fecha nula, código duplicado, etc.
- `AlmacenLlenoException` (propia, verificada): no hay anaqueles libres. Se lanza con `throw`, se declara con `throws` en `GestorAlmacen.registrarPaquete` y se captura en `Main`.
- Los registros y entregas rechazados no cambian el estado del almacén (se valida antes de modificar).

## Reproducir los casos de prueba
Ejecutar `java -cp out PruebasAlmacen` cubre P1 a P6. Para P7 y las capturas del menú, ejecutar `java -cp out app.Main`:

| Caso | Cómo reproducirlo en el menú |
|---|---|
| P1 y P2 | Opción 8, o registrar un paquete de cada tipo y usar la opción 5 |
| P3 | Opción 1 y escribir `abc` como peso |
| P4 | Opción 1 con peso `0` o `-1` |
| P5 | Registrar 3 paquetes (anaqueles llenos) y un cuarto: `AlmacenLlenoException`; revisar con la opción 7 |
| P6 | Después del fallo, registrar un paquete válido (o entregar uno y registrar otro) |
| P7 | Un registro válido y uno fallido: «Intento finalizado.» aparece en ambos; «Registrado con éxito» solo en el válido |

## Limitaciones conocidas
- Los datos solo existen en memoria; al cerrar el programa se pierden.

## Integrantes
- Bolaños Fuentes Juan Manuel
- Prado Gutierrez Juan Alejandro
- Sánchez Navarro Rodrigo
- Villa De Jesús Roberto

Último commit: Tercer Avance parcial
