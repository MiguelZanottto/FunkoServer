package develop.services;

import develop.models.Funko;
import develop.models.Model;
import develop.services.funkos.FunkosCacheImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


public class FunkoCacheTest {
    private FunkosCacheImpl funkoCache;

    @BeforeEach
    void setUp() throws SQLException {
        funkoCache = new FunkosCacheImpl(10);
        funkoCache.clear();
    }

    @AfterEach
    void tearDown() throws SQLException {
        funkoCache.clear();
    }

    @Test
    void putFunko() throws ExecutionException, InterruptedException {
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        int tamanoEsperado = 1;

        funkoCache.put(funko.getId(), funko).block();

        assertAll(() -> assertTrue(funkoCache.getTamano() > 0)); // Comprobamos que se ha añadido 1 elemento a la cache
    }


    @Test
    void getFunko() throws ExecutionException, InterruptedException {
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        funkoCache.put(funko.getId(), funko).block();  // añadimos el elemento a la cache

        Optional<Funko> funkoFound = funkoCache.get(1L).blockOptional();  // Recuperamos el elemento de la cache

        assertAll(() -> assertTrue(funkoFound.isPresent()),  // Comprobamos que se ha recuperado efectivamente el elemento y que posee los atributos esperados
                () -> assertEquals(funko.getId(), funkoFound.get().getId()),
                () -> assertEquals(funko.getCOD(), funkoFound.get().getCOD()),
                () -> assertEquals(funko.getName(), funkoFound.get().getName()),
                () -> assertEquals(funko.getModel(), funkoFound.get().getModel()),
                () -> assertEquals(funko.getPrice(), funkoFound.get().getPrice()),
                () -> assertEquals(funko.getReleaseData(), funkoFound.get().getReleaseData()),
                () -> assertEquals(funko.getUpdatedAt(), funkoFound.get().getUpdatedAt()),
                () -> assertEquals(funko.getCreatedAt(), funkoFound.get().getCreatedAt())
                );
    }



    @Test
    void getFunkoNoExiste() throws ExecutionException, InterruptedException {
        Optional<Funko> funkoFound = funkoCache.get(99L).blockOptional();   // Intentamos recuperar un elemento que no se encuentra guardado en la cache
        assertAll(() -> assertFalse(funkoFound.isPresent()));    // Comprobamos que el elemento no se ha encontrado en la cache ya que no se encuentra guardado
    }


    @Test
    void removeFunko() throws ExecutionException, InterruptedException {
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        funkoCache.put(funko.getId(), funko).block(); // Añadiendo funko a cache

        funkoCache.remove(funko.getId()).block(); // Removiendo funko de cache

        assertAll(() -> assertFalse(funkoCache.get(funko.getId()).blockOptional().isPresent()));  // Comprobando que el funko se ha eliminado de la cache
    }


    @Test
    void removeFunkoNoExiste() throws ExecutionException, InterruptedException {
        Funko funko = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test")
                .model(Model.OTROS)
                .price(9.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        funkoCache.put(funko.getId(), funko).block(); // Añadiendo funko a cache
        int tamanoCache = funkoCache.getTamano(); // Tamano cache = 1
        funkoCache.remove(99L).block(); // Removiendo funko de cache que no existe

        assertAll(() -> assertEquals(tamanoCache, funkoCache.getTamano()));  // El tamano sigue siendo 1 porque no se ha eliminado ningun funko
    }


    @Test
    void clearTest() throws ExecutionException, InterruptedException {
        Funko funko1 = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test-1")
                .model(Model.OTROS)
                .price(19.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        Funko funko2 = Funko.builder()
                .id(2L)
                .COD(UUID.randomUUID())
                .name("Test-2")
                .model(Model.ANIME)
                .price(99.99)
                .releaseData(LocalDate.of(2020, 4, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        funkoCache.put(funko1.getId(), funko1).block(); // Añadiendo funko1 a cache
        funkoCache.put(funko2.getId(), funko2).block(); // Añadiendo funko2 a cache

        int tamanoEsperado = 0;

        Thread.sleep((60*2*1000) + 5000); // Paramos el hilo 2 minutos para que se ejecute el clear de forma automatica
        assertAll(() -> assertFalse(funkoCache.get(funko1.getId()).blockOptional().isPresent()),  // Comprobando que el funko1 ha sido eliminado de la cache
                ()-> assertFalse(funkoCache.get(funko2.getId()).blockOptional().isPresent()),     // Comprobando que el funko2 ha sido eliminado de la cache
                ()-> assertEquals(tamanoEsperado, funkoCache.getTamano()));                 // Comprobamos que el tamano de la cache ahora es 0
    }


    @Test
    void clearTest2() throws ExecutionException, InterruptedException {
        Funko funko1 = Funko.builder()
                .id(1L)
                .COD(UUID.randomUUID())
                .name("Test-1")
                .model(Model.OTROS)
                .price(19.99)
                .releaseData(LocalDate.of(2020, 1, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        Funko funko2 = Funko.builder()
                .id(2L)
                .COD(UUID.randomUUID())
                .name("Test-2")
                .model(Model.ANIME)
                .price(99.99)
                .releaseData(LocalDate.of(2020, 4, 1))
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        funkoCache.put(funko1.getId(), funko1).block(); // Añadiendo funko1 a cache
        funkoCache.put(funko2.getId(), funko2).block(); // Añadiendo funko2 a cache

        Thread.sleep(60*1*1000); // Paramos el hilo 1 minuto para evidenciar que el clear no se ha ejecutado
        assertAll(() -> assertTrue(funkoCache.get(funko1.getId()).blockOptional().isPresent()),  // Comprobando que el funko1 no ha sido eliminado de la cache
                ()-> assertTrue(funkoCache.get(funko2.getId()).blockOptional().isPresent()),     // Comprobando que el funko2 no ha sido eliminado de la cache
                ()-> assertNotEquals(0, funkoCache.getTamano()));                 // Comprobamos que el tamano de la cache no es 0
    }
}
