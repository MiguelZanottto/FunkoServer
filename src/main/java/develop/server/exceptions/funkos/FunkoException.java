package develop.server.exceptions.funkos;

public abstract class FunkoException extends Exception {
    public FunkoException(String message) {
        super(message);
    }
}