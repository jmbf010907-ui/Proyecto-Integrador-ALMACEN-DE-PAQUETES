# Almacén de Paquetes

Proyecto Integrador de Programación Orientada a Objetos (Java) — Facultad de Ingeniería, Grupo 4.

Sistema de gestión de un almacén de paquetes: registro, asignación de anaquel, búsqueda, entrega, historial y pendientes.

## Estructura
```
.github/workflows/   CI: compila, corre pruebas y Main en cada push
src/
├── modelo/          Paquete, Remitente, Destinatario, Anaquel, Envio, Entrega
├── servicio/        GestorAlmacen
└── app/             Main
tests/               PruebasAlmacen.java
docs/                Plantilla del PDF, UML y evidencias
INSTRUCTIONS.md      Instrucciones del avance y reparto del equipo
RUBRIC.md            Rúbrica de evaluación
```

## Requisitos
JDK 17 o superior.

## Compilar y ejecutar
```
javac -d out src/modelo/*.java src/servicio/*.java src/app/*.java tests/*.java
java -cp out app.Main
java -cp out PruebasAlmacen
```

## Integrantes
- Bolaños Fuentes Juan Manuel
- Prado Gutierrez Juan Alejandro
- Sánchez Navarro Rodrigo
- Villa De Jesús Roberto
