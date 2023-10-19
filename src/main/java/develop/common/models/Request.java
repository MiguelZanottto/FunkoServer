package develop.common.models;

public record Request(Type type, String content, String token, String createdAt) {
    public enum Type {
        LOGIN, SALIR, OTRO, GETALL, GETBYID, GETBYMODEL, GETBYRELEASEDATA, POST, UPDATE, DELETE
    }
}