package develop.server.repositories.funkos;


import develop.common.models.Funko;
import develop.server.repositories.crud.CrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Interfaz que extiende la operacion CRUD (Create, Read, Update, Delete) para la entidad Funko.
 */
public interface FunkosRepository extends CrudRepository<Funko, Long> {

    /**
     * Busca un Funko por su UUID unico.
     *
     * @param uuid El UUID del Funko que se busca.
     * @return Un Mono que emite el Funko encontrado o un valor vacio si no se encuentra.
     */
    Mono<Funko> findByUuid(UUID uuid);

    /**
     * Busca Funkos por su nombre.
     *
     * @param nombre El nombre de los Funkos que se buscan.
     * @return Un Flux que emite los Funkos encontrados.
     */
    Flux<Funko> findByNombre(String nombre);
}
