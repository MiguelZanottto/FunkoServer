package develop.repositories.funkos;

import develop.common.models.Funko;
import develop.common.models.IdGenerator;
import develop.common.models.Model;
import develop.server.repositories.funkos.FunkosRepository;
import develop.server.repositories.funkos.FunkosRepositoryImpl;
import develop.server.services.services.database.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

 class FunkosRepositoryTest {
    private FunkosRepository funkosRepository;

    @BeforeEach
    void setup()  {
        funkosRepository = FunkosRepositoryImpl.getInstance(DatabaseManager.getInstance(), IdGenerator.getInstance());
        DatabaseManager.getInstance().initTables();
        IdGenerator.getInstance().resetId();
    }

    @Test
    void saveFunko(){
        Funko funko = Funko.builder()
                .id(1L)
                .cod(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();

        Funko savedFunko = funkosRepository.save(funko).block();

        assertAll(()-> assertNotNull(savedFunko),
                () -> assertEquals(1, savedFunko.getId()),
                () -> assertEquals(1, savedFunko.getId()),
                () -> assertEquals(funko.getCod(), savedFunko.getCod()),
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
                .cod(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko savedFunko = funkosRepository.save(funko).block();
        Optional<Funko> foundFunko = funkosRepository.findById(savedFunko.getId()).blockOptional();

        assertAll(() -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(1, foundFunko.get().getId()),
                () -> assertEquals(1, foundFunko.get().getMyId()),
                () -> assertEquals(funko.getCod(), foundFunko.get().getCod()),
                () -> assertEquals(funko.getName(), foundFunko.get().getName()),
                () -> assertEquals(funko.getModel(), foundFunko.get().getModel()),
                () -> assertEquals(funko.getPrice(), foundFunko.get().getPrice()),
                () -> assertEquals(funko.getReleaseData(), foundFunko.get().getReleaseData()),
                () -> assertNotNull(foundFunko.get().getCreatedAt()),
                () -> assertNotNull(foundFunko.get().getUpdatedAt())
        );
    }


    @Test
    void findFunkoByIdNoExiste()  {
        Optional<Funko> foundFunko = funkosRepository.findById(1L).blockOptional();
        assertAll(() -> assertFalse(foundFunko.isPresent())
        );
    }

    @Test
    void findFunkoByUuid(){
        UUID uuid = UUID.randomUUID();
        Funko funko = Funko.builder()
                .id(1L)
                .cod(uuid)
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko savedFunko = funkosRepository.save(funko).block();
        Optional<Funko> foundFunko = funkosRepository.findByUuid(savedFunko.getCod()).blockOptional();

        assertAll(() -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(1, foundFunko.get().getId()),
                () -> assertEquals(1, foundFunko.get().getMyId()),
                () -> assertEquals(funko.getCod(), foundFunko.get().getCod()),
                () -> assertEquals(funko.getName(), foundFunko.get().getName()),
                () -> assertEquals(funko.getModel(), foundFunko.get().getModel()),
                () -> assertEquals(funko.getPrice(), foundFunko.get().getPrice()),
                () -> assertEquals(funko.getReleaseData(), foundFunko.get().getReleaseData()),
                () -> assertNotNull(foundFunko.get().getCreatedAt()),
                () -> assertNotNull(foundFunko.get().getUpdatedAt())
        );
    }

    @Test
    void findAllFunkos()  {
        // Arrange
        Funko funko1 = Funko.builder()
                .cod(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko funko2 = Funko.builder()
                .cod(UUID.randomUUID())
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
    void findFunkosByNombre()  {
        // Arrange
        Funko funko1 = Funko.builder()
                .cod(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko funko2 = Funko.builder()
                .cod(UUID.randomUUID())
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
                () -> assertEquals(foundFunko.get(0).getCod(), funko1.getCod()),
                () -> assertEquals(foundFunko.get(0).getName(), funko1.getName()),
                () -> assertEquals(foundFunko.get(0).getModel(), funko1.getModel()),
                () -> assertEquals(foundFunko.get(0).getPrice(), funko1.getPrice()),
                () -> assertEquals(foundFunko.get(0).getReleaseData(), funko1.getReleaseData()),
                () -> assertEquals(foundFunko.get(1).getCod(), funko2.getCod()),
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
                .cod(UUID.randomUUID())
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
                .cod(UUID.randomUUID())
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
                .cod(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .build();
        Funko funko2 = Funko.builder()
                .cod(UUID.randomUUID())
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
