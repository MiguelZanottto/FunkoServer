package develop.server.services.services.storage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
/**
 * Interfaz que define operaciones para importar datos desde una fuente de almacenamiento y emitirlos como Flux.
 *
 * @param <T> El tipo de elementos que se almacenan y se emiten desde esta fuente de almacenamiento.
 */
public interface Storage<T> {

    /**
     * Importa datos desde una fuente de almacenamiento (por ejemplo, un archivo CSV) y los emite como elementos de un Flux.
     *
     * @return Un Flux que emite elementos del tipo T importados desde la fuente de almacenamiento.
     */
    Flux<T> importCsv();
}
