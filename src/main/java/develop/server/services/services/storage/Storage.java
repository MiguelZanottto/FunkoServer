package develop.server.services.services.storage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface Storage<T> {

    Mono<Void> exportJson(List<T> data, String file);

    Flux<T> importCsv();
}