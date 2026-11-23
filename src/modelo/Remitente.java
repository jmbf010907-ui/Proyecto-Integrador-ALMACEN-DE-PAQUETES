package modelo;

/**
 * Persona o negocio que deja el paquete en el almacén.
 *
 * Por qué se conserva (observación del primer y segundo avance): el destinatario es quien
 * recoge, pero el remitente es quien dejó el paquete y a quien el almacén debe avisar cuando
 * un paquete lleva varios días sin ser recogido. La consulta de pendientes (opción 6 del menú,
 * GestorAlmacen.getPendientes) muestra el nombre y teléfono del remitente para contactarlo.
 * Sin esta clase el almacén no tendría a quién reportar un paquete rezagado.
 */
public class Remitente {
    private String nombre;
    private String telefono;

    public Remitente(String nombre, String telefono) {
        setNombre(nombre);
        setTelefono(telefono);
    }

    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del remitente no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public void setTelefono(String telefono) {
        if (telefono == null || !telefono.matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono debe tener 10 dígitos.");
        }
        this.telefono = telefono;
    }

    @Override
    public String toString() { return nombre + " (" + telefono + ")"; }
}
