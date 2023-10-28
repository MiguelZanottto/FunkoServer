package develop.server.exceptions.server;

/**
 * Excepcion general para los problemas que tengan que ver con el servidor.
 */
public class ServerException extends Exception {
    /**
     * Crea una nueva instancia de ServerException con el mensaje de error especificado.
     *
     * @param message El mensaje de error que describe la excepcion.
     */
    public ServerException(String message) {
        super(message);
    }
}
