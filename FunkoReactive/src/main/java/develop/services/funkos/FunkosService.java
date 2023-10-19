package develop.services.funkos;

import develop.exceptions.funkos.FunkoNoEncotradoException;
import develop.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FunkosService {
    Flux<Funko> findAll();

    Flux<Funko> findAllByNombre(String nombre);

    Mono<Funko> findById(long id);

    Mono<Funko> save(Funko funko);

    Mono<Funko> update(Funko funko);

    Mono<Funko> deleteById(long id);

    Mono<Void> deleteAll();

    Mono<Funko> findByUuid(UUID uuid);
}

