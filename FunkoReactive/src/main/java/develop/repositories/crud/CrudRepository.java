package develop.repositories.crud;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Optional;

public interface CrudRepository<T, ID> {

    Mono<T> save(T t);

    Mono<T> update(T t);

    Mono<T> findById(ID id);

    Flux<T> findAll();

    Mono<Boolean> deleteById(ID id);

    Mono<Void> deleteAll();
}
