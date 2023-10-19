package develop.server.exceptions.storage;

public abstract class StorageException extends Exception  {

    public StorageException(String message) {
        super(message);
    }
}
