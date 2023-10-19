package develop.repositories.funkos;

import develop.models.Funko;
import develop.repositories.crud.CrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.UUID;

public interface FunkosRepository  extends CrudRepository<Funko, Long> {

    Mono<Funko> findByUuid(UUID uuid);
    Flux<Funko> findByNombre(String nombre);
}
