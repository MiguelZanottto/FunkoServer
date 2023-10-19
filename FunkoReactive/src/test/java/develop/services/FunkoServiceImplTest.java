package develop.services;

import develop.exceptions.funkos.FunkoNoAlmacenadoException;
import develop.exceptions.funkos.FunkoNoEncotradoException;
import develop.exceptions.storage.RutaInvalidaException;
import develop.models.Funko;
import develop.models.Model;
import develop.repositories.funkos.FunkosRepository;
import develop.services.funkos.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    @Mock
    FunkosRepository repository;

    @Mock
    FunkoStorage storage;

    @Mock
    FunkosNotification notification;

    @InjectMocks
    FunkosServiceImpl service;


    @Test
    void findAll() {
        // Arrange
        var funkos = List.of(
                Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build(),
                Funko.builder().COD(UUID.randomUUID()).name("Test-2").model(Model.MARVEL).price(19.99).releaseData(LocalDate.of(2021, 1, 1)).build()
        );

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));

        // Act
        var result = service.findAll().collectList().block();

        // Assert
        assertAll("findAll",
                () -> assertEquals(result.size(), 2, "No se han recuperado dos funkos"),
                () -> assertEquals(result.get(0).getName(), "Test-1", "El primer funko no es el esperado"),
                () -> assertEquals(result.get(1).getName(), "Test-2", "El segundo funko no es el esperado"),
                () -> assertEquals(result.get(0).getModel(), Model.OTROS, "El modelo del primer funko no es el esperado"),
                () -> assertEquals(result.get(1).getModel(), Model.MARVEL, "El modelo del segundo funko no es el esperado"),
                () -> assertEquals(result.get(0).getPrice(), 9.99, "El precio del primer funko no es el esperado"),
                () -> assertEquals(result.get(1).getPrice(), 19.99, "El precio del segundo funko no es el esperado"),
                () -> assertEquals(result.get(0).getReleaseData(), LocalDate.of(2020, 1, 1), "La fecha de creacion del primer funko no es el esperado"),
                () -> assertEquals(result.get(1).getReleaseData(),LocalDate.of(2021, 1, 1), "La fecha de creacion del segundo funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findAll();
    }



    @Test
    void findAllByNombre() {
        // Arrange
        var funkos = List.of(
                Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build()
        );

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findByNombre("Test-1")).thenReturn(Flux.fromIterable(funkos));

        // Act8
        var result = service.findAllByNombre("Test-1").collectList().block();


        // Assert
        assertAll("findAllByNombre",
                () -> assertEquals(result.size(), 1, "No se ha recuperado un funko"),
                () -> assertEquals(result.get(0).getName(), "Test-1", "El funko no es el esperado"),
                () -> assertEquals(result.get(0).getModel(), Model.OTROS, "El modelo del funko no es el esperado"),
                () -> assertEquals(result.get(0).getPrice(), 9.99, "El precio del funko no es el esperado"),
                () -> assertEquals(result.get(0).getReleaseData(), LocalDate.of(2020, 1, 1), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findByNombre("Test-1");
    }

    @Test
    void findById()  {
        // Arrange
        var funko = Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.just(funko)); // Simulamos que lo devuelve del repositorio

        // Act
        var result = service.findById(1L).blockOptional();

        // Assert
        assertAll("findById",
                () -> assertTrue(result.isPresent(), "El funko no es el esperado"),
                () -> assertEquals(result.get().getName(), "Test-1", "El funko no es el esperado"),
                () -> assertEquals(result.get().getModel(), Model.OTROS, "El modelo del funko no es el esperado"),
                () -> assertEquals(result.get().getPrice(), 9.99, "El precio del funko no es el esperado"),
                () -> assertEquals(result.get().getReleaseData(), LocalDate.of(2020, 1, 1), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).findById(1L);
    }



    @Test
    void findByIdNoExiste() {
        // Arrange
        var funko = Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

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
        Funko funko = Funko.builder().id(1L).COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findByUuid(funko.getCOD())).thenReturn(Mono.just(funko));
        when(repository.save(funko)).thenReturn(Mono.just(funko));

        // Act
        var result = service.saveWithoutNotification(funko).block();

        // Assert
        assertAll("save",
                () -> assertNotNull(result, "El resultado es nulo"),
                () -> assertEquals(result.getName(), "Test-1", "El funko no es el esperado"),
                () -> assertEquals(result.getModel(), Model.OTROS, "El modelo del funko no es el esperado"),
                () -> assertEquals(result.getPrice(), 9.99, "El precio del funko no es el esperado"),
                () -> assertEquals(result.getReleaseData(), LocalDate.of(2020, 1, 1), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio y del cache
        verify(repository, times(1)).save(funko);
    }


    @Test
    void update(){
        // Arrange
        var funko = Funko.builder().id(1L).COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findById(1L)).thenReturn(Mono.just(funko));
        when(repository.update(funko)).thenReturn(Mono.just(funko));

        // Act
        var result = service.update(funko).block();

        // Assert
        assertAll("update",
                () -> assertNotNull(result, "El resultado es nulo"),
                () -> assertEquals(result.getName(), "Test-1", "El funko no es el esperado"),
                () -> assertEquals(result.getModel(), Model.OTROS, "El modelo del funko no es el esperado"),
                () -> assertEquals(result.getPrice(), 9.99, "El precio del funko no es el esperado"),
                () -> assertEquals(result.getReleaseData(), LocalDate.of(2020, 1, 1), "La fecha de creacion del funko no es el esperado")
        );

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).update(funko);
    }


    @Test
    void deleteById() {
        // Arrange
        var funko = Funko.builder().id(1L).COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

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
        // Arrange
        var funko = Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

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
    void deleteAll() throws SQLException, ExecutionException, InterruptedException {
        // Arrange
        var funko = Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build();

        // Cuando se llame al método al repositorio simulamos...
        when(repository.deleteAll()).thenReturn(Mono.empty());

        // Act
        service.deleteAll().block();

        // Comprobamos que se ha llamado al método del repositorio
        verify(repository, times(1)).deleteAll();
    }


    @Test
    void export() throws IOException, RutaInvalidaException, SQLException, ExecutionException, InterruptedException, FunkoNoAlmacenadoException {
        // Arrange
        String file = "funkos_test.json";
        var funkos = List.of(
                Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build(),
                Funko.builder().COD(UUID.randomUUID()).name("Test-2").model(Model.MARVEL).price(19.99).releaseData(LocalDate.of(2021, 1, 1)).build()
        );

        // Cuando se llame al método al repositorio simulamos...
        when(repository.findAll()).thenReturn(Flux.fromIterable(funkos));
        when(storage.exportJson(funkos, file)).thenReturn(Mono.empty());

        // Act
        service.exportFile(file).block();

        // Comprobamos que se ha llamado al método del repositorio y del almacenamiento
        verify(storage, times(1)).exportJson(funkos, file);
        verify(repository, times(1)).findAll();
    }

        @Test
        void importFile() throws IOException, SQLException, ExecutionException, InterruptedException {
        // Arrange
        List<Funko> funkos;
        var listaFunkos = List.of(
                    Funko.builder().COD(UUID.randomUUID()).name("Test-1").model(Model.OTROS).price(9.99).releaseData(LocalDate.of(2020, 1, 1)).build(),
                    Funko.builder().COD(UUID.randomUUID()).name("Test-2").model(Model.MARVEL).price(19.99).releaseData(LocalDate.of(2021, 1, 1)).build()
        );

        //  Cuando se llama al metodo importCsv...
        when(storage.importCsv()).thenReturn(Flux.fromIterable(listaFunkos));

        // Act
        funkos = service.importFile().collectList().block();

        // Assert
        assertAll("import",
                () -> assertTrue(funkos != null),
                () -> assertEquals(funkos.size(), listaFunkos.size(), "Las listas tienen tamanos distintos"),
                () -> assertEquals(funkos.get(0).getCOD(), listaFunkos.get(0).getCOD(), "El COD de los objetos en la lista son distintos"),
                () -> assertEquals(funkos.get(1).getCOD(), listaFunkos.get(1).getCOD(), "El COD de los objetos en la lista son distintos"),
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

