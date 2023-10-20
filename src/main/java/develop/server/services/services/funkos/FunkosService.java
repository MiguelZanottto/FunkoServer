package develop.server.services.services.funkos;

import develop.common.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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

