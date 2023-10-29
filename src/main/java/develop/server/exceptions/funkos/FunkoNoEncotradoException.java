package develop.server.exceptions.funkos;

/**
 * Excepcion que se lanza cuando no se encuentra un Funko en el servidor.
 */
public class FunkoNoEncotradoException extends FunkoException {
    /**
     * Crea una nueva instancia de FunkoNoEncotradoException con el mensaje de error especificado.
     *
     * @param message El mensaje de error que describe la excepcion.
     */
    public FunkoNoEncotradoException(String message) {
        super(message);
    }
}
