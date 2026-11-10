# Instrucciones del equipo — Tercer avance

## Objetivo
Evolucionar el segundo avance para que distintos tipos de objetos compartan una estructura y respondan de forma diferente a una misma operación (**herencia y polimorfismo**), y que el sistema valide los datos, atienda situaciones excepcionales y conserve un estado coherente cuando una operación falle (**excepciones**).

- Entrega: en Classroom, en la fecha indicada por el profesor (reporte PDF + enlace del repositorio o carpeta comprimida).
- Los tres entregables (PDF, código y README) deben corresponder a **la misma versión**; el commit de entrega se anota en el README y en el reporte.

## Requisitos
- ≥ 4 clases funcionales (sin contar Main ni la excepción propia) y ≥ 3 paquetes.
- ≥ 4 funcionalidades: las 2 del segundo avance (corregidas) y ≥ 2 adicionales.
- Menú de consola con operaciones sucesivas y opción de salir (datos en memoria).
- Una clase base y ≥ 2 subclases con relación «es un» justificada; `this` y `super(...)`.
- Método sobrescrito con `@Override` en ambas subclases y llamado mediante el tipo base.
- Entrada con formato incorrecto (`NumberFormatException`), `IllegalArgumentException` y una **excepción propia verificada** (`extends Exception`) con `throw`, `throws` y captura en `Main`.
- ≥ 1 bloque `try-catch-finally`.
- Validar antes de modificar; tras un fallo se puede seguir usando el sistema.
- 7 casos de prueba (P1 a P7) con estado inicial, acción, esperado, obtenido y evidencia.
- UML actualizado (generalización hacia la clase base; la excepción hereda de `Exception`).


## Decisiones del equipo
| Tema | Decisión |
|---|---|
| Jerarquía | `Paquete` (base) → `PaqueteEstandar` / `PaqueteFragil` |
| Método polimórfico | `calcularCostoAlmacenaje(int dias)` |
| Regla estándar | 3 días gratis y 5 pesos por día |
| Regla frágil | cargo de manejo + 12 pesos por día |
| Excepción propia | `AlmacenLlenoException extends Exception` (paquete `excepciones`) |


## Reparto (cada quien edita solo sus archivos)
| Integrante | Rama | Archivos | Reporte |
|---|---|---|---|
| Juan Manuel | `dev/juan-manuel` | `src/app/Main.java`, README, integración y merges | Portada y secciones 1, 2 y 8 |
| Juan Alejandro | `dev/juan-alejandro` | `src/modelo/` Paquete, PaqueteEstandar, PaqueteFragil, Remitente, Destinatario | Secciones 3 y 4 |
| Rodrigo | `dev/rodrigo` | `src/modelo/Anaquel.java`, `src/excepciones/`, UML en `docs/` | Secciones 5 y 6 |
| Roberto | `dev/roberto` | `src/servicio/GestorAlmacen.java`, `tests/`, `docs/evidencias/` | Sección 7 |

## Reglas
1. No se sube directo a `main`: cada quien trabaja en su rama y abre Pull Request.
2. Otro compañero revisa antes del merge.
3. Antes del PR, el código compila y las pruebas pasan (lo verifica la Action de GitHub: ✓ verde).
4. Commits pequeños con mensaje claro.
5. Todos deben poder **explicar su parte** (la revisión puede incluir preguntas individuales).

## Orden de trabajo
1. Cada integrante sube su parte a su rama y la compila.
2. Juan Manuel integra las ramas en `main`, compila todo y corre `PruebasAlmacen`.
3. Cada quien toma las capturas de los casos que le tocan (legibles, con el número de caso).
4. Se arma el PDF, se anota el commit de entrega y se revisa la lista de verificación.

## Lista de verificación final
- [ ] El proyecto compila y se ejecuta siguiendo el README.
- [ ] Hay ≥ 4 clases del dominio o servicio y 3 paquetes.
- [ ] Hay clase base, dos subclases y polimorfismo observable.
- [ ] La excepción propia se lanza, se declara y se captura en una operación real.
- [ ] Los fallos controlados conservan el estado y permiten seguir usando el sistema.
- [ ] El UML, el código, el reporte y las evidencias corresponden a la misma versión.
- [ ] Commit de entrega anotado en el README y en el reporte.

## Pendientes (avances posteriores)

- Asignación automática de anaqueles por dimensiones, peso o categoría, y consolidación de envíos.

