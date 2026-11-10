package excepciones;

/**
 * Excepción verificada que indica que un anaquel (o el almacén) no tiene
 * espacio disponible para resguardar un paquete.
 *
 * Al heredar de Exception (y no de RuntimeException), el compilador obliga a
 * declararla con throws y a capturarla en la capa que coordina la interacción.
 */
public class AlmacenLlenoException extends Exception {

    public AlmacenLlenoException(String mensaje) {
        super(mensaje);
    }
}
