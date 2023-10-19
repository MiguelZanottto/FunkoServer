package develop.services.cache;

import develop.models.Funko;
import reactor.core.publisher.Mono;

public interface Cache<K, V> {

    Mono <Void> put(K key, V value);

    Mono<Funko> get(K key);

    Mono<Void> remove(K key);

    void clear();

    void shutdown();
}