package develop.repositories.users;

import develop.common.models.User;
import develop.server.repositories.users.UsersRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class UsersRepositoryTest {
    UsersRepository usersRepository = UsersRepository.getInstance();

    @Test
    void testFindByUsername_userExist() {
        // Arrange
        String username = "pepe"; // El usuario pepe esta definido en la lista del Repositorio de Usuarios

        // Act
        Optional <User> userFound = usersRepository.findByByUsername(username);

        assertAll(
                () -> assertNotNull(userFound, "Usuario nulo"),
                () -> assertTrue(userFound.isPresent())
        );
    }

    @Test
    void testFindByUsername_userNotExist() {
        // Arrange
        String username = "laura"; // El usuario laura no esta definido en la lista del Repositorio de Usuarios

        // Act
        Optional <User> userFound = usersRepository.findByByUsername(username);

        assertAll(
                () -> assertNotNull(userFound, "Usuario nulo"),
                () -> assertTrue(userFound.isEmpty(), "El usuario no esta vacio")
        );
    }

    @Test
    void testFindById_idExist() {
        // Arrange
        int id = 1; // El usuario con id 1 esta definido en la lista del Repositorio de Usuarios

        // Act
        Optional <User> userFound = usersRepository.findByById(id);

        assertAll(
                () -> assertNotNull(userFound, "Usuario nulo"),
                () -> assertTrue(userFound.isPresent())
        );
    }

    @Test
    void testFindById_idNotExist() {
        // Arrange
        int id = 99; // El usuario con id 99 no esta definido en la lista del Repositorio de Usuarios

        // Act
        Optional <User> userFound = usersRepository.findByById(id);

        assertAll(
                () -> assertNotNull(userFound, "Usuario nulo"),
                () -> assertTrue(userFound.isEmpty(), "El usuario no esta vacio")
        );
    }
}
