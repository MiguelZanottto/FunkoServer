package develop.common.models;

/**
 * Clase que representa un usuario con un identificador, nombre de usuario, contrasena y rol.
 */
public record User(long id, String username, String password, Role role) {
    /**
     * Enumeracion que define los roles posibles de un usuario.
     */
    public enum Role {
        ADMIN,  // Rol de administrador
        USER    // Rol de usuario
    }
}
