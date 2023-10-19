package develop.repositories;

import develop.exceptions.funkos.FunkoNoAlmacenadoException;
import develop.exceptions.funkos.FunkoNoEncotradoException;
import develop.models.Funko;
import develop.models.IdGenerator;
import develop.models.Model;
import develop.repositories.funkos.FunkosRepository;
import develop.repositories.funkos.FunkosRepositoryImpl;
import develop.services.database.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FunkosRepositoryImplTest {
    private FunkosRepository funkosRepository;

    @BeforeEach
    void setup() throws SQLException {
        funkosRepository = FunkosRepositoryImpl.getInstance(DatabaseManager.getInstance(), IdGenerator.getInstance());
        DatabaseManager.getInstance().initTables();
        IdGenerator.getInstance().resetId();
    }

    @Test
    void saveFunko(){
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();

        Funko savedFunko = funkosRepository.save(funko).block();

        assertAll(()-> assertNotNull(savedFunko),
                () -> assertNotNull(savedFunko.getId()),
                () -> assertNotNull(savedFunko.getMyId()),
                () -> assertEquals(funko.getCOD(), savedFunko.getCOD()),
                () -> assertEquals(funko.getName(), savedFunko.getName()),
                () -> assertEquals(funko.getModel(), savedFunko.getModel()),
                () -> assertEquals(funko.getPrice(), savedFunko.getPrice()),
                () -> assertEquals(funko.getReleaseData(), savedFunko.getReleaseData()),
                () -> assertNotNull(savedFunko.getCreatedAt()),
                () -> assertNotNull(savedFunko.getUpdatedAt())
        );
    }

    @Test
    void findFunkoById() {
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko savedFunko = funkosRepository.save(funko).block();
        Optional<Funko> foundFunko = funkosRepository.findById(savedFunko.getId()).blockOptional();

        assertAll(() -> assertTrue(foundFunko.isPresent()),
                () -> assertNotNull(foundFunko.get().getId()),
                () -> assertNotNull(foundFunko.get().getMyId()),
                () -> assertEquals(funko.getCOD(), foundFunko.get().getCOD()),
                () -> assertEquals(funko.getName(), foundFunko.get().getName()),
                () -> assertEquals(funko.getModel(), foundFunko.get().getModel()),
                () -> assertEquals(funko.getPrice(), foundFunko.get().getPrice()),
                () -> assertEquals(funko.getReleaseData(), foundFunko.get().getReleaseData()),
                () -> assertNotNull(foundFunko.get().getCreatedAt()),
                () -> assertNotNull(foundFunko.get().getUpdatedAt())
        );
    }


    @Test
    void findFunkoByIdNoExiste() throws SQLException, ExecutionException, InterruptedException, FunkoNoEncotradoException {
        Optional<Funko> foundFunko = funkosRepository.findById(1L).blockOptional();
        assertAll(() -> assertFalse(foundFunko.isPresent())
        );
    }



    @Test
    void findAllFunkos() throws SQLException, ExecutionException, InterruptedException, FunkoNoAlmacenadoException {
        // Arrange
        Funko funko1 = Funko.builder()
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko funko2 = Funko.builder()
                .COD(UUID.randomUUID())
                .name("Test1")
                .model(Model.MARVEL)
                .price(19.99)
                .releaseData(LocalDate.of(2021, 1, 1))
                .build();

        funkosRepository.save(funko1).block();
        funkosRepository.save(funko2).block();

        // Act
        List<Funko> foundFunkos = funkosRepository.findAll().collectList().block();


        // Asserts
        assertEquals(2, foundFunkos.size());
    }


    @Test
    void findFunkosByNombre() throws SQLException, ExecutionException, InterruptedException, FunkoNoAlmacenadoException, FunkoNoEncotradoException {
        // Arrange
        Funko funko1 = Funko.builder()
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko funko2 = Funko.builder()
                .COD(UUID.randomUUID())
                .name("Test1")
                .model(Model.MARVEL)
                .price(19.99)
                .releaseData(LocalDate.of(2021, 1, 1))
                .build();

        funkosRepository.save(funko1).block();
        funkosRepository.save(funko2).block();

        // Act
        List<Funko> foundFunko = funkosRepository.findByNombre("Test").collectList().block();

        // Assert
        assertAll(() -> assertNotNull(foundFunko),
                () -> assertEquals(2, foundFunko.size()),
                () -> assertEquals(foundFunko.get(0).getCOD(), funko1.getCOD()),
                () -> assertEquals(foundFunko.get(0).getName(), funko1.getName()),
                () -> assertEquals(foundFunko.get(0).getModel(), funko1.getModel()),
                () -> assertEquals(foundFunko.get(0).getPrice(), funko1.getPrice()),
                () -> assertEquals(foundFunko.get(0).getReleaseData(), funko1.getReleaseData()),
                () -> assertEquals(foundFunko.get(1).getCOD(), funko2.getCOD()),
                () -> assertEquals(foundFunko.get(1).getName(), funko2.getName()),
                () -> assertEquals(foundFunko.get(1).getPrice(), funko2.getPrice()),
                () -> assertEquals(foundFunko.get(1).getModel(), funko2.getModel()),
                () -> assertEquals(foundFunko.get(1).getReleaseData(), funko2.getReleaseData())
        );
    }


    @Test
    void updateFunko(){
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko savedFunko = funkosRepository.save(funko).block();
        savedFunko.setName("Updated");
        savedFunko.setModel(Model.ANIME);
        savedFunko.setPrice(99.99);
        savedFunko.setUpdatedAt(LocalDateTime.now());

        // Act
        funkosRepository.update(savedFunko).block();
        Optional<Funko> foundFunko = funkosRepository.findById(savedFunko.getId()).blockOptional();

        // Asserts
        assertAll(() -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(savedFunko.getName(), foundFunko.get().getName()),
                () -> assertEquals(savedFunko.getModel(), foundFunko.get().getModel()),
                () -> assertEquals(savedFunko.getPrice(), foundFunko.get().getPrice()),
                () -> assertNotEquals(savedFunko.getUpdatedAt(), foundFunko.get().getUpdatedAt()) // El updated tiene que ser diferente de la actual, ya que se modifica automaticamente al llamar al update
        );
    }

    @Test
    void deleteFunko(){
        // Arrange
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko savedFunko = funkosRepository.save(funko).block();

        // Act
        funkosRepository.deleteById(savedFunko.getId()).block();
        Optional<Funko> foundFunko = funkosRepository.findById(savedFunko.getId()).blockOptional();

        // Asserts
        assertFalse(foundFunko.isPresent());
    }

    @Test
    void deleteAllFunkos(){
        // Arrange
        Funko funko1 = Funko.builder()
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko funko2 = Funko.builder()
                .COD(UUID.randomUUID())
                .name("Test1")
                .model(Model.MARVEL)
                .price(19.99)
                .releaseData(LocalDate.of(2021, 1, 1))
                .build();

        funkosRepository.save(funko1).block();
        funkosRepository.save(funko2).block();

        // Act
        funkosRepository.deleteAll().block();
        List<Funko> foundAlumnos = funkosRepository.findAll().collectList().block();

        // Asserts
        assertEquals(0, foundAlumnos.size());
    }
}
