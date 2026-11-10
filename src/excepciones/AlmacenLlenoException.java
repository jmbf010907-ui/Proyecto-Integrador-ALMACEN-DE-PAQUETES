package excepciones;

/** Se usa cuando se quiere guardar un paquete y no hay anaqueles libres. */
public class AlmacenLlenoException extends Exception {
    public AlmacenLlenoException(String mensaje) {
        super(mensaje); // envía el mensaje a Exception
    }
}
