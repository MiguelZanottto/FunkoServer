package develop.common.models;

/**
 * Clase que representa una solicitud con un tipo, contenido, token y marca de tiempo de creacion.
 */
public record Request(Type type, String content, String token, String createdAt) {
    /**
     * Enumeracion que define los tipos posibles de solicitud.
     */
    public enum Type {
        LOGIN,           // Solicitud de inicio de sesion
        SALIR,           // Solicitud para salir o cerrar sesion
        OTRO,            // Otro tipo de solicitud
        GETALL,          // Solicitud para obtener todos los elementos
        GETBYID,         // Solicitud para obtener por ID
        GETBYMODEL,      // Solicitud para obtener por modelo
        GETBYRELEASEDATA, // Solicitud para obtener por fecha de lanzamiento
        POST,            // Solicitud para crear (POST)
        UPDATE,          // Solicitud para actualizar (PUT)
        DELETE           // Solicitud para eliminar (DELETE)
    }
}
