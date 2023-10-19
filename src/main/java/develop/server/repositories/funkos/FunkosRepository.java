package develop.server.repositories.funkos;


import develop.common.models.Funko;
import develop.server.repositories.crud.CrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FunkosRepository extends CrudRepository<Funko, Long> {

    Mono<Funko> findByUuid(UUID uuid);
    Flux<Funko> findByNombre(String nombre);
}
