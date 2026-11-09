# Rúbrica — Tercer avance

| Criterio | Valor | Evidencia para obtener el puntaje completo |
|---|---|---|
| Evolución funcional | 15 % | Al menos cuatro funcionalidades útiles, continuidad con el segundo avance y correcciones documentadas. |
| Herencia y constructores | 15 % | Clase base y dos subclases pertinentes; relación justificada; uso correcto de `this` y `super(...)`. |
| Polimorfismo | 20 % | Método sobrescrito en ambas subclases, llamadas mediante el tipo base y diferencias útiles verificadas en ejecución. |
| Validaciones y excepciones | 20 % | Formato, argumentos y excepción propia atendidos; `throw`, `throws` y `finally` correctos; estado consistente y recuperación. |
| Encapsulamiento, UML y paquetes | 10 % | Atributos protegidos mediante acceso controlado, responsabilidades claras y UML coherente con el código. |
| Pruebas y evidencias | 10 % | Siete casos mínimos reproducibles, resultados esperados y obtenidos, y evidencias de las cuatro funcionalidades. |
| Documentación y comprensión | 10 % | Reporte y README completos; participación identificada y explicación clara de las decisiones y del código. |
| **Total** | **100 %** | |

## Escala por criterio
- Completo y correcto: 100 % de su valor.
- Adecuado con detalles menores: 80 %.
- Parcial: 60 %.
- Insuficiente: 0 a 40 %, según la evidencia.

El mismo fallo no se descuenta dos veces por el mismo motivo.

## Casos mínimos de prueba
| Caso | Situación | Qué debe comprobarse |
|---|---|---|
| P1 | Operación válida con un objeto de la primera subclase | Se llama al método mediante el tipo base y se observa el comportamiento de esa subclase. |
| P2 | Misma llamada con un objeto de la segunda subclase | Se ejecuta su implementación y se observa la diferencia prevista. |
| P3 | Texto en una entrada numérica | Se captura la excepción de formato y se informa cómo corregir el dato. |
| P4 | Valor fuera de las reglas (cero, negativo, vacío) | Se lanza y captura `IllegalArgumentException` sin aceptar datos inválidos. |
| P5 | Situación que activa la excepción propia | Se informa el motivo y se conserva el estado previo (anotar estado antes y después). |
| P6 | Nueva operación válida después de un fallo | El sistema continúa y ejecuta la nueva solicitud correctamente. |
| P7 | Un intento válido y uno fallido con `finally` | La acción de `finally` aparece en ambos; la confirmación de éxito solo en el válido. |

## Entregables
- Reporte en PDF (portada, cambios y observaciones atendidas, funcionalidades, jerarquía, polimorfismo, UML y paquetes, validaciones y excepciones, tabla de pruebas con evidencias, participación y pendientes).
- Proyecto Java completo (código fuente en paquetes, `Main`, clases base y derivadas, excepción propia).
- `README.md` (versión de Java, estructura, cómo compilar y ejecutar, funcionalidades, pasos para reproducir las pruebas y limitaciones).