# Instrucciones del equipo: cuarto avance (entrega final)

## Objetivo
Cerrar el proyecto: atender la retroalimentación del tercer avance, dejar las cuatro funcionalidades integradas y estables, documentar P1 a P8 y preparar la exposición (máximo 5 minutos) y la demostración en vivo.

## Retroalimentación atendida
| Observación | Dónde quedó |
|---|---|
| Prueba en el límite de días gratis (3 días = $0, 4 días = $5) mostrando ambas fechas y que cotizar no cambia nada | `PruebasAlmacen` (bloque «frontera»), opción 5 del menú y `docs/evidencias/E3_frontera_dias_gratis.salida.txt` |
| Fecha nula no debe ocupar el anaquel; después debe poder registrarse otro en el mismo anaquel | `GestorAlmacen.registrarPaquete` y bloque «Prueba sugerida» de `PruebasAlmacen` |
| Proteger los cambios de estado del paquete | `Paquete` sin `setEstado`/`setAnaquel` públicos; `Anaquel.ocupar/liberar` y `Envio.cerrar` son los únicos que cambian estado y ubicación |
| Justificar `Remitente` | Es a quien se avisa de paquetes pendientes (opción 6); explicado en `Remitente.java`, README y reporte |

## Reparto
| Integrante | Rama | Archivos | Exposición |
|---|---|---|---|
| Juan Manuel | `dev/juan-manuel` | `Main`, `Remitente`, `Destinatario`, README, integración y merges | Diapositivas 1 y 5 (problema y cierre) |
| Juan Alejandro | `dev/juan-alejandro` | `Paquete`, `PaqueteEstandar`, `PaqueteFragil` | Diapositiva 3 (herencia y polimorfismo) |
| Rodrigo | `dev/rodrigo` | `Anaquel`, `Envio`, `excepciones/`, UML | Diapositiva 2 (diseño y UML) |
| Roberto | `dev/roberto` | `GestorAlmacen`, `tests/`, `docs/evidencias/` | Diapositiva 4 (excepciones y pruebas) |

## Antes de entregar
1. Integrar `dev/juan-manuel` en `main` con Pull Request (la Action debe quedar en verde).
2. En `main`: compilar desde cero con el README y correr `java -cp out PruebasAlmacen`.
3. Crear la etiqueta `entrega-final` sobre ese commit y subirla (`git tag entrega-final` y `git push origin entrega-final`).
4. Subir a Classroom: reporte PDF, presentación y enlace del repositorio con la etiqueta.

## Guion de la demostración en vivo (después de los 5 minutos)
1. Compilar y abrir el menú siguiendo el README.
2. Opción 1 con `abc` como peso (P3), luego con `-1` (P4) y luego un registro válido (P6 y P7).
3. Opción 2: registrar un frágil. Opción 8 u opción 5 para ambos tipos (P1 y P2).
4. Llenar los 3 anaqueles y registrar un cuarto: `AlmacenLlenoException`; opción 7 antes y después (P5).
5. Opción 6 (pendientes con remitente), opción 4 (entrega) y un nuevo registro en el anaquel liberado (P8).
6. Abrir el código: constructores con `super(...)`, `@Override` en las subclases, `registrarPaquete` (validar antes de modificar), `Anaquel.ocupar` y `Envio.cerrar`, `AlmacenLlenoException` y el `try-catch-finally` de `Main.registrar`.

## Lista de verificación
- [ ] Compila y ejecuta desde cero siguiendo el README.
- [ ] Cuatro funcionalidades reales con menú y opción de salir.
- [ ] Al menos 4 clases funcionales además de `Main` y 3 paquetes.
- [ ] Clase base, dos subclases y polimorfismo comprobable.
- [ ] Validaciones, excepción propia y `try-catch-finally`.
- [ ] Un fallo controlado no corrompe el estado y permite continuar.
- [ ] UML, reporte PDF y evidencias de P1 a P8.
- [ ] Exposición de 5 minutos como máximo; la demostración va después.
- [ ] Todos pueden explicar su parte y una decisión del código.
