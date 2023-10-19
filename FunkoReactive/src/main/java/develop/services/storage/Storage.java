package develop.services.storage;


import develop.exceptions.storage.RutaInvalidaException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface Storage<T> {

    Mono<Void> exportJson(List<T> data, String file) throws IOException;

    Flux<T> importCsv() throws IOException;
}