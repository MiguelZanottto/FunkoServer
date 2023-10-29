package develop.server.exceptions.funkos;

/**
 * Clase abstracta para excepciones relacionadas con Funkos en el servidor.
 */
public abstract class FunkoException extends Exception {
    /**
     * Crea una nueva instancia de FunkoException con el mensaje de error especificado.
     *
     * @param message El mensaje de error que describe la excepcion.
     */
    public FunkoException(String message) {
        super(message);
    }
}
