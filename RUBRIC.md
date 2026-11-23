# Rúbrica: cuarto avance (entrega final)

| Criterio | Valor | Evidencia de cumplimiento |
|---|---|---|
| Versión final y funcionalidades | 25 % | Cuatro funcionalidades completas, flujo estable, mejoras y correcciones respecto al avance 3. |
| Diseño y aplicación de POO | 25 % | Encapsulamiento, clases, paquetes, herencia, constructores, `@Override`, polimorfismo y excepciones utilizados correctamente. |
| Pruebas y manejo de fallos | 15 % | P1–P8 verificables, resultados consistentes y recuperación ante errores. |
| Código y documentación | 10 % | Compila, README útil, UML congruente, reporte completo y evidencia de participación. |
| Exposición (máximo 5 min) | 10 % | Claridad, dominio del proyecto, administración del tiempo y participación del equipo. |
| Demostración en vivo | 15 % | Ejecución real, cuatro funciones, polimorfismo, excepción, recuperación y explicación del código. |
| **Total** | **100 %** | |

Escala por criterio: completo 100 %; adecuado con detalles menores 80 %; parcial 60 %; insuficiente 0–40 %.

## Pruebas mínimas de cierre
| Caso | Situación | Resultado que debe comprobarse |
|---|---|---|
| P1–P2 | Polimorfismo con las dos subclases | Misma llamada mediante tipo base; comportamientos distintos. |
| P3 | Entrada de formato incorrecto | Error controlado y mensaje entendible. |
| P4 | Dato que viola una regla | `IllegalArgumentException` y rechazo correcto. |
| P5 | Excepción propia | Estado anterior y posterior sin cambios indebidos. |
| P6 | Acción válida después del error | El programa continúa operando. |
| P7 | `finally` en éxito y fallo | La acción final ocurre en ambos intentos. |
| P8 | Flujo completo con varias acciones | Las cuatro funcionalidades se conectan correctamente. |

## Entregables
1. Reporte final en PDF (5 a 9 páginas de contenido + anexos): `docs/Reporte_Final_Almacen_de_Paquetes.pdf`.
2. Código Java completo (este repositorio, etiqueta `entrega-final`).
3. `README.md` actualizado.
4. Presentación de 4 a 6 diapositivas (se sube aparte a Classroom; no está en el repositorio).
