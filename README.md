# Almacén de Paquetes

Proyecto Integrador de Programación Orientada a Objetos (Java), Facultad de Ingeniería, Grupo 4.

Sistema de consola para un almacén que recibe paquetes, los guarda en anaqueles, los entrega al destinatario, cotiza el costo de almacenaje y avisa qué paquetes llevan varios días sin recogerse.
**Versión: cuarto avance, entrega final.** Etiqueta de entrega en Git: `entrega-final`.

## Requisitos
- JDK 11 o superior (probado con JDK 21). No usa bibliotecas externas.
- Cualquier terminal (PowerShell, CMD, bash) o VS Code con la extensión de Java.

## Compilar y ejecutar
Desde la carpeta raíz del proyecto (la que contiene `src` y `tests`).

**Linux, macOS o Git Bash:**
```
javac -encoding UTF-8 -d out src/modelo/*.java src/servicio/*.java src/excepciones/*.java src/app/*.java tests/*.java
java -cp out app.Main
java -cp out PruebasAlmacen
```

**Windows (PowerShell o CMD):**
```
javac -encoding UTF-8 -d out src\modelo\*.java src\servicio\*.java src\excepciones\*.java src\app\*.java tests\*.java
java -cp out app.Main
java -cp out PruebasAlmacen
```
Si los acentos se ven mal en la consola de Windows, ejecuta `chcp 65001` antes.

## Estructura
```
src/
├── modelo/        Paquete (base), PaqueteEstandar, PaqueteFragil,
│                  Remitente, Destinatario, Anaquel, Envio, Entrega
├── servicio/      GestorAlmacen (reglas del almacén)
├── excepciones/   AlmacenLlenoException (propia, hereda de Exception)
└── app/           Main (menú de consola)
tests/             PruebasAlmacen.java (P1 a P8, 71 verificaciones)
docs/              UML (dot/png/svg), reporte final y evidencias
docs/evidencias/   Entradas reproducibles (entradas/*.txt) y sus salidas (*.salida.txt)
.github/workflows/ CI: compila, corre las pruebas y el flujo P8 en cada push
```

## Menú y funcionalidades
| Opción | Qué hace | Funcionalidad |
|---|---|---|
| 1 / 2 | Registrar paquete estándar / frágil y asignarle el primer anaquel libre | **F1 Registro** |
| 4 | Registrar la entrega: cierra el envío, marca el paquete ENTREGADO y libera el anaquel | **F2 Entrega** |
| 5 | Cotizar el costo de almacenaje (polimórfico); muestra ingreso, fecha de consulta y días | **F3 Costo** |
| 6 | Ver pendientes (N o más días) con el remitente a quien avisar | **F4 Pendientes** |
| 3 | Buscar un paquete por código | complementaria |
| 7 | Ver historial de envíos y estado de los anaqueles | complementaria |
| 8 | Demostración de polimorfismo (dos objetos con referencia `Paquete`) | demostración |
| 0 | Salir | |

- Fechas con formato `AAAA-MM-DD`; con Enter se usa la fecha de hoy.
- El almacén tiene 3 anaqueles (A-01 a A-03). Los datos viven en memoria durante la sesión.

### Reglas principales
- **Estándar:** 3 días gratis y después 5 pesos por día (3 días = $0, 4 días = $5).
- **Frágil:** cargo de manejo + 12 pesos por día desde el primer día.
- Peso mayor a 0, código no vacío y único, teléfonos de 10 dígitos, fecha de ingreso obligatoria.
- La entrega no puede ser anterior al ingreso ni repetirse.
- **Validar antes de modificar:** ningún rechazo deja un anaquel ocupado, un envío a medias o un estado cambiado.
- El estado y el anaquel de un `Paquete` no tienen setters públicos: solo `Anaquel.ocupar/liberar` y `Envio.cerrar` los cambian, siempre de forma coherente.
- **Remitente:** es a quien el almacén avisa cuando un paquete lleva días sin recogerse (opción 6).

## Datos de ejemplo y reproducción de pruebas
Las pruebas automáticas cubren P1 a P8 (P7 y P8 ejecutan el menú real con entrada simulada):
```
java -cp out PruebasAlmacen
```
Cada sesión de evidencia se puede repetir redirigiendo su archivo de entrada:
```
java -cp out app.Main < docs/evidencias/entradas/E5_P8_flujo_completo.txt
```
En PowerShell: `Get-Content docs\evidencias\entradas\E5_P8_flujo_completo.txt | java -cp out app.Main`

| Caso | Qué comprueba | Entrada | Salida |
|---|---|---|---|
| P1–P2 | Misma llamada con referencia `Paquete`: estándar $10, frágil $80 a 5 días | `E2_P1_P2_polimorfismo.txt` | `E2_…salida.txt` |
| P1 frontera | Estándar a 3 días $0 y a 4 días $5; cotizar no cambia envío ni anaquel | `E3_frontera_dias_gratis.txt` | `E3_…salida.txt` |
| P3 | `abc` como peso → mensaje de formato | `E1_P3_P4_P6_P7_registro.txt` | `E1_…salida.txt` |
| P4 | Peso `-1` → `IllegalArgumentException` | `E1_…` | `E1_…` |
| P5 | Cuarto paquete con 3 anaqueles llenos → `AlmacenLlenoException`; historial igual antes y después | `E4_P5_P6_almacen_lleno.txt` | `E4_…salida.txt` |
| P6 | Tras el fallo, entrega y nuevo registro en el anaquel liberado | `E4_…` | `E4_…` |
| P7 | «Intento finalizado.» en el intento fallido y en el válido | `E1_…` | `E1_…` |
| P8 | Registro → pendientes → costo → entrega → nuevo registro → historial | `E5_P8_flujo_completo.txt` | `E5_…salida.txt` |

## Conceptos de POO
- **Herencia:** `PaqueteEstandar` y `PaqueteFragil` extienden `Paquete` y llaman a `super(...)`.
- **Polimorfismo:** `calcularCostoAlmacenaje(int dias)` tiene `@Override` en ambas subclases; `GestorAlmacen` y la opción 8 lo llaman con referencias de tipo `Paquete`.
- **Encapsulamiento:** atributos `private`; validación en constructores y setters; cambios de estado con visibilidad de paquete.
- **Excepciones:** `NumberFormatException`, `DateTimeParseException`, `IllegalArgumentException`, `IllegalStateException` y la propia `AlmacenLlenoException` (verificada: `throw` en el gestor, `throws` en la firma y captura en `Main` con `try-catch-finally`).

## Elementos opcionales
No se usaron patrones de diseño formales, hilos, archivos ni bases de datos. La única automatización extra es el flujo de GitHub Actions.

## Limitaciones conocidas
- Los datos solo existen en memoria; al cerrar el programa se pierden.
- El número de anaqueles es fijo (3) y se define en `Main`.
- Se asigna el primer anaquel libre; no se considera tamaño, peso ni categoría.
- El estado del paquete y del envío se guarda como texto con constantes (no como `enum`).
- Las tarifas están fijas en el código.

## Integrantes
- Bolaños Fuentes Juan Manuel
- Prado Gutierrez Juan Alejandro
- Sánchez Navarro Rodrigo
- Villa De Jesús Roberto
