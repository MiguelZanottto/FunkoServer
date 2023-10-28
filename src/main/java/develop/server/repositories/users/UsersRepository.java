package develop.server.repositories.users;

import develop.common.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

/**
 * Un Repositorio que almacena usuarios en memoria, como Pepe y Ana.
 */
public class UsersRepository {
    private static UsersRepository INSTANCE = null;
    private final List<User> users = List.of(
            new User(
                    1,
                    "pepe",
                    BCrypt.hashpw("pepe1234", BCrypt.gensalt(12)),
                    User.Role.ADMIN
            ),
            new User(
                    2,
                    "ana",
                    BCrypt.hashpw("ana1234", BCrypt.gensalt(12)),
                    User.Role.USER
            )
    );

    private UsersRepository() {
    }

    /**
     * Obtiene la instancia unica del repositorio de usuarios.
     * @return La instancia del repositorio de usuarios
     */
    public synchronized static UsersRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsersRepository();
        }
        return INSTANCE;
    }

    /**
     * Busca un usuario por su nombre
     * @param username El nombre de usuario a buscar
     * @return Un Optional que contiene el usuario encontrado o nada si no se encuentra el nombre.
     */
    public Optional<User> findByByUsername(String username) {
        return users.stream()
                .filter(user -> user.username().equals(username))
                .findFirst();
    }

    /**
     * Busca un usuario por su ID
     * @param id El ID del usuario a buscar.
     * @return Un Optional que contiene el usuario encontrado o nada si no se encuentra el ID.
     */
    public Optional<User> findByById(int id) {
        return users.stream()
                .filter(user -> user.id() == id)
                .findFirst();
    }
}