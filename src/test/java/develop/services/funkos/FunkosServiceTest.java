package develop.services.funkos;

import develop.common.models.Funko;
import develop.common.models.Model;
import develop.server.repositories.funkos.FunkosRepository;
import develop.server.services.services.funkos.FunkosNotification;
import develop.server.services.services.funkos.FunkosServiceImpl;
import develop.server.services.services.funkos.FunkosStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkosServiceTest {

    @Mock
    FunkosRepository repository;

    @Mock
    FunkosStorage storage;

    @Mock
    FunkosNotification notification;

    @InjectMocks
    FunkosServiceImpl service;


    @Test
    void findAll() {
        // Arrange
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build(),
                Funko.builder().cod(UUID.randomUUID()).name("Test-2").model(Model.MARVEL).price(19.99).releaseData(LocalDate.of(2021, 1, 1)).build()
        );

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));

        // Act
        var result = service.findAll().collectList().block();

        // Assert
        assertAll("findAll",
                () -> assertFalse(result.isEmpty(), "La lista esta vacia"),
                () -> assertEquals("Test-1", result.get(0).getName(), "El primer funko no es el esperado"),
                () -> assertEquals("Test-2", result.get(1).getName(), "El segundo funko no es el esperado"),
                () -> assertEquals(Model.OTROS, result.get(0).getModel(), "El modelo del primer funko no es el esperado"),
                () -> assertEquals(Model.MARVEL, result.get(1).getModel(), "El modelo del segundo funko no es el esperado"),
                () -> assertEquals(9.99, result.get(0).getPrice(), "El precio del primer funko no es el esperado"),
                () -> assertEquals(19.99, result.get(1).getPrice(), "El precio del segundo funko no es el esperado"),
                () -> assertEquals(LocalDate.of(2020, 1, 1), result.get(0).getReleaseData(), "La fecha de creacion del primer funko no es el esperado"),
                () -> assertEquals(LocalDate.of(2021, 1, 1), result.get(1).getReleaseData(), "La fecha de creacion del segundo funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAllByNombre() {
        // Arrange
        var funkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build()
        );

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findByNombre("Test-1")).thenReturn(Flux.fromIterable(funkos));

        // Act8
        var result = service.findAllByNombre("Test-1").collectList().block();


        // Assert
        assertAll("findAllByNombre",
                () -> assertEquals(1, result.size(), "No se ha recuperado un funko"),
                () -> assertEquals("Test-1", result.get(0).getName(), "El funko no es el esperado"),
                () -> assertEquals(Model.OTROS, result.get(0).getModel(), "El modelo del funko no es el esperado"),
                () -> assertEquals(9.99, result.get(0).getPrice(), "El precio del funko no es el esperado"),
                () -> assertEquals(LocalDate.of(2020, 1, 1), result.get(0).getReleaseData(), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findByNombre("Test-1");
    }

    @Test
    void findById()  {
        // Arrange
        var funko = Funko.builder().cod(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.just(funko)); // Simulamos que lo devuelve del repositorio

        // Act
        var result = service.findById(1L).blockOptional();

        // Assert
        assertAll("findById",
                () -> assertTrue(result.isPresent(), "El funko no es el esperado"),
                () -> assertEquals("Test-1", result.get().getName(), "El funko no es el esperado"),
                () -> assertEquals(Model.OTROS, result.get().getModel(), "El modelo del funko no es el esperado"),
                () -> assertEquals(9.99, result.get().getPrice(), "El precio del funko no es el esperado"),
                () -> assertEquals(LocalDate.of(2020, 1, 1), result.get().getReleaseData(), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findByIdNoExiste() {
        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.empty());

        // Act
        var res = assertThrows(Exception.class, () -> service.findById(1L).blockOptional());

        // Assert
        assertTrue(res.getMessage().contains("Funko con id 1 no encontrado"));

        // Comprobamos que se ha llamado el método del repositorio
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void save(){
        // Arrange
        Funko funko = Funko.builder().id(1L).cod(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findByUuid(funko.getCod())).thenReturn(Mono.just(funko));
        when(repository.save(funko)).thenReturn(Mono.just(funko));

        // Act
        var result = service.saveWithoutNotification(funko).block();

        // Assert
        assertAll("save",
                () -> assertNotNull(result, "El resultado es nulo"),
                () -> assertEquals("Test-1", result.getName(), "El funko no es el esperado"),
                () -> assertEquals(Model.OTROS, result.getModel(), "El modelo del funko no es el esperado"),
                () -> assertEquals(9.99, result.getPrice() , "El precio del funko no es el esperado"),
                () -> assertEquals(LocalDate.of(2020, 1, 1), result.getReleaseData(), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio y del cache
        verify(repository, times(1)).save(funko);
    }

    @Test
    void update(){
        // Arrange
        var funko = Funko.builder().id(1L).cod(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        when(repository.update(funko)).thenReturn(Mono.just(funko));

        // Act
        var result = service.update(funko).block();

        // Assert
        assertAll("update",
                () -> assertNotNull(result, "El resultado es nulo"),
                () -> assertEquals("Test-1", result.getName(), "El funko no es el esperado"),
                () -> assertEquals(Model.OTROS, result.getModel() , "El modelo del funko no es el esperado"),
                () -> assertEquals(9.99, result.getPrice(), "El precio del funko no es el esperado"),
                () -> assertEquals(LocalDate.of(2020, 1, 1), result.getReleaseData(), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).update(funko);
    }

    @Test
    void deleteById() {
        // Arrange
        var funko = Funko.builder().id(1L).cod(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        when(repository.deleteById(1L)).thenReturn(Mono.just(true));

        // Act
        var result = service.deleteById(1L).block();

        // Assert
        assertEquals(result, funko,"No se ha borrado el funko");

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteNoExiste()  {
        // Cuando se llame al método al repositorio simulamos la excepción FunkoNoEncotradoException...
        when(repository.findById(1L)).thenReturn(Mono.empty());

        // Act
        var res = assertThrows(Exception.class, () -> service.deleteById(1L).blockOptional());

        // Assert
        assertTrue(res.getMessage().contains("Funko con id 1 no encontrado"));

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deleteAll()  {
        // Cuando se llame al método al repositorio simulamos...
        when(repository.deleteAll()).thenReturn(Mono.empty());

        // Act
        service.deleteAll().block();

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).deleteAll();
    }

    @Test
    void importFile()  {
        // Arrange
        List<Funko> funkos;
        var listaFunkos = List.of(
                Funko.builder().cod(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build(),
                Funko.builder().cod(UUID.randomUUID()).name("Test-2").model(Model.MARVEL).price(19.99).releaseData(LocalDate.of(2021, 1, 1)).build()
        );

        //  Cuando se llama al metodo importCsv...
        when(storage.importCsv()).thenReturn(Flux.fromIterable(listaFunkos));

        // Act
        funkos = service.importFile().collectList().block();

        // Assert
        assertAll("import",
                () -> assertNotNull(funkos),
                () -> assertFalse(funkos.isEmpty(), "La lista de funkos esta vacia"),
                () -> assertEquals(funkos.get(0).getCod(), listaFunkos.get(0).getCod(), "El COD de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(1).getCod(), listaFunkos.get(1).getCod(), "El COD de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(0).getName(), listaFunkos.get(0).getName(), "El nombre de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(1).getName(), listaFunkos.get(1).getName(), "El nombre de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(0).getPrice(), listaFunkos.get(0).getPrice(), "El precio de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(1).getPrice(), listaFunkos.get(1).getPrice(), "El precio de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(0).getModel(), listaFunkos.get(0).getModel(), "El modelo de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(1).getModel(), listaFunkos.get(1).getModel(), "El modelo de los objetos en la lista son distintos" ),
                () -> assertEquals(funkos.get(0).getReleaseData(), listaFunkos.get(0).getReleaseData(), "La fecha de creacion de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(1).getReleaseData(), listaFunkos.get(1).getReleaseData(), "La fecha de los objetos en la lista son distintos"));

        // Comprobamos que se ha llamado al metodo del almacenamiento
        verify(storage, times(1)).importCsv();
    }
}

