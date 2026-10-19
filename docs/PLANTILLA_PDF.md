# Plantilla del documento PDF (Segundo avance)

Cada responsable llena su sección en este archivo (rama propia). Al final se junta todo en un solo PDF.

## Portada
Facultad de Ingeniería - Programación Orientada a Objetos
Proyecto: Almacén de Paquetes | Grupo 4
Integrantes: Bolaños Fuentes Juan Manuel, Prado Gutierrez Juan Alejandro, Sánchez Navarro Rodrigo, Villa De Jesús Roberto
Fecha de entrega: 18 de octubre

---

## 1. Revisión del primer avance  — Responsable: Juan Alejandro
- Síntesis de la problemática: La gestión de un almacén de paquetes presenta retrasos y errores en el movimiento de la mercancía, desde que entra hasta que sale para su entrega. Se busca reducir el tiempo y el margen de error en ese proceso.
- Objetivos: Diseñar e implementar, a lo largo del semestre, un sistema de gestión de almacén que reduzca los tiempos de recorrido y procesamiento de paquetes mediante la automatización de la asignación de ubicaciones y la consolidación de envíos. Esta primera versión sienta las bases: el registro, la ubicación y el control de estado de los paquetes. La automatización y el algoritmo de optimización se incorporarán en avances posteriores.
- Alcance: Incluido: registro de paquetes, información de remitentes y destinatarios, asignación y consulta de ubicaciones, control del estado de los paquetes y registro de entregas. Excluido en esta etapa: seguimiento GPS, rutas de transporte, pagos, compras en línea y comunicación con empresas de paquetería.
- Ajustes realizados a partir de la retroalimentación:
  - Del primer avance se pasaron a código seis de los siete objetos preliminares: Paquete, Remitente, Destinatario, Anaquel, Envio y Entrega.
  - El objeto Operador no se implementó en esta etapa porque no aporta lógica propia todavía; queda como siguiente paso.
  - Las ocho funcionalidades propuestas se concentraron en la clase de servicio GestorAlmacen; se demuestran dos de ellas, como pide el avance.
  - Se agregó una carpeta de pruebas y un flujo automático de GitHub (CI) que compila y ejecuta el proyecto en cada cambio.
  - Se hicieron cambios en el apartado de almacen, asi haciendo mejoras en ese apartado tal y como se menciono en la primera retroalimentación.


## 2. Catálogo de clases — Responsable: Juan Alejandro (modelo) y Roberto (servicio)
Para cada clase: responsabilidad, atributos, métodos, relaciones.

### Paquete
- Responsabilidad: Representa un envío individual guardado en el almacén: su código, peso, estado, remitente, destinatario y anaquel asignado. Valida sus propios datos y inicia en estado EN_ALMACEN.
- Atributos:
  - `+EN_ALMACEN: String` {static, final}
  - `+ENTREGADO: String` {static, final}
  - `-codigo: String`
  - `-pesoKg: double`
  - `-estado: String`
  - `-remitente: Remitente`
  - `-destinatario: Destinatario`
  - `-anaquel: Anaquel`
- Métodos:
  - `+Paquete(codigo: String, pesoKg: double, remitente: Remitente, destinatario: Destinatario)`
  - `+getCodigo(): String`
  - `+getPesoKg(): double`
  - `+getEstado(): String`
  - `+getRemitente(): Remitente`
  - `+getDestinatario(): Destinatario`
  - `+getAnaquel(): Anaquel`
  - `+setAnaquel(anaquel: Anaquel): void`
  - `+setEstado(estado: String): void`
  - `+toString(): String`
- Validaciones: Código no vacío; peso mayor a 0; remitente y destinatario obligatorios (IllegalArgumentException).
- Relaciones: Tiene un Remitente y un Destinatario; se guarda en 0..1 Anaquel; es el objeto principal de un Envio.

### Remitente
- Responsabilidad: Representa a la persona o negocio que trae el paquete al almacén. Guarda sus datos de contacto y valida que sean correctos.
- Atributos:
  - `-nombre: String`
  - `-telefono: String`
- Métodos:
  - `+Remitente(nombre: String, telefono: String)`
  - `+getNombre(): String`
  - `+getTelefono(): String`
  - `+setNombre(nombre: String): void`
  - `+setTelefono(telefono: String): void`
  - `+toString(): String`
- Validaciones: Nombre no vacío; teléfono de 10 dígitos (IllegalArgumentException). El constructor usa los setters, por lo que la validación está en un solo lugar.
- Relaciones: Asociada con Paquete (un remitente puede tener varios paquetes; cada paquete tiene un remitente).

### Destinatario
- Responsabilidad: Representa a la persona que recogerá el paquete. Guarda su nombre y teléfono y valida que sean correctos.
- Atributos:
  - `-nombre: String`
  - `-telefono: String`
- Métodos:
  - `+Destinatario(nombre: String, telefono: String)`
  - `+getNombre(): String`
  - `+getTelefono(): String`
  - `+setNombre(nombre: String): void`
  - `+setTelefono(telefono: String): void`
  - `+toString(): String`
- Validaciones: Nombre no vacío; teléfono de 10 dígitos (IllegalArgumentException). El constructor usa los setters, por lo que la validación está en un solo lugar.
- Relaciones: Asociada con Paquete (un destinatario puede recibir varios paquetes; cada paquete tiene un destinatario).

### Anaquel
### Envio
### Entrega
### GestorAlmacen
### Operador (si se implementa)

## 3. Diagrama UML — Responsable: Rodrigo
Insertar imagen en `docs/uml.png`. Debe mostrar atributos con tipos, métodos con parámetros y retorno, visibilidad (+/−) y relaciones.

## 4. Organización en paquetes — Responsable: Juan Manuel
Estructura de carpetas y por qué: `modelo` (datos), `servicio` (lógica), `app` (ejecución).

## 5. Implementación Java — Responsable: Juan Manuel
Decisiones: atributos privados, constructores con validación, getters/setters, imports entre paquetes.

## 6. Funcionalidades y pruebas — Responsable: Roberto

| Prueba | Entrada | Resultado esperado | Resultado obtenido | Evidencia |
|---|---|---|---|---|
| 1. Registrar paquete y asignar anaquel | Remitente: Tienda La Esquina (5512345678), Destinatario: Ana López (5587654321), Paquete PQ-001 (2.5 kg, fecha 10/10/2026). Intento adicional con peso -1. | Paquete 
registrado en estado EN_ALMACEN en el anaquel A-01 con envío ACTIVO. El registro con peso -1 se rechaza con error controlado. | Paquete asignado exitosamente a A-01 en estado ACTIVO. Rechazo del peso negativo con mensaje de error controlado y aparición en pendientes (3+ días). | docs/evidencias/prueba1.png |
| 2. Registrar entrega | Código PQ-001, recibido por: Ana López, fecha: 15/10/2026. | Paquete pasa a ENTREGADO sin anaquel; envío pasa a CERRADO con fecha y receptor; anaquel A-01 queda libre. Todas las pruebas automatizadas pasan. | Entrega registrada correctamente, envío CERRADO, anaquel A-01 liberado y suite de pruebas unitarias ejecutada con 13 verificaciones en [OK]. | docs/evidencias/prueba2.png |

## 7. Reflexión y plan — Responsable: Roberto

- **Decisiones de diseño:**
  - **Separación entre Paquete y Envío:** El `Paquete` representa el objeto físico, mientras que `Envio` gestiona el trámite y su estado (ACTIVO/CERRADO), permitiendo conservar el historial aunque el paquete haya sido entregado.
  - **Asociación bidireccional entre Anaquel y Paquete:** Permite identificar rápidamente qué paquete ocupa una ubicación o liberar un anaquel sin necesidad de recorrer listas completas.
  - **Centralización en GestorAlmacen:** Las clases de la capa `modelo` guardan datos y validaciones simples, mientras que las reglas de negocio del almacén residen en la capa `servicio`.
  - **Pruebas deterministas con fechas pasadas por parámetro:** Se utilizan objetos `LocalDate` como argumentos en lugar de leer la fecha del sistema, garantizando que las pruebas sean siempre reproducibles.

- **Problemas técnicos encontrados:**
  - **Liberación limpia de anaqueles:** Se debió asegurar que al cambiar el estado del paquete a `ENTREGADO`, la referencia al anaquel se liberara correctamente sin causar errores de puntero nulo.
  - **Manejo de excepciones en casos de error:** Se implementaron bloques `try-catch` para capturar `IllegalArgumentException` (pesos negativos, códigos duplicados) e `IllegalStateException` (almacén lleno) y verificar que las reglas de negocio detengan registros inválidos.

- **Plan para avances posteriores:**
  - Incorporar la clase `Operador` para registrar la identidad del empleado responsable de cada recepción y entrega.
  - Desarrollar algoritmos de asignación automática de anaqueles basados en dimensiones, peso o categoría de los paquetes.
  - Implementar un menú interactivo en consola y persistencia de datos (archivos o base de datos).
