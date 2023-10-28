package develop.common.models;

/**
 * Clase que representa una respuesta con un estado, contenido y marca de tiempo de creacion.
 */
public record Response(Status status, String content, String createdAt) {
    /**
     * Enumeracion que define los estados posibles de respuesta.
     */
    public enum Status {
        OK,     // Respuesta exitosa
        ERROR,  // Respuesta de error
        BYE,    // Respuesta de cierre de sesion
        TOKEN   // Respuesta que contiene un token
    }
}
