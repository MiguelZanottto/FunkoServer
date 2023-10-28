package develop.client.exceptions;
/**
 * Esta clase representa la excepcion que se ejecutara cuando ocurra un error con un Cliente.
 * Extebduebdi de la clase Exception
 * @version 1.0
 */
public class ClientException extends Exception {
    public ClientException(String message) {
        super(message);
    }
}