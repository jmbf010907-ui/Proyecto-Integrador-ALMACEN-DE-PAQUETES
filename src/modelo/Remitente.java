package modelo;

/** Persona o negocio que trae el paquete al almacén. */
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
        this.nombre = nombre;
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
