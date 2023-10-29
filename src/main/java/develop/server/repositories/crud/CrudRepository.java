package develop.server.repositories.crud;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Interfaz generica que define operaciones CRUD (Create, Read, Update, Delete) para una entidad y su identificador.
 *
 * @param <T> El tipo de entidad.
 * @param <ID> El tipo del identificador de la entidad.
 */
public interface CrudRepository<T, ID> {

    /**
     * Guarda una entidad en el repositorio.
     *
     * @param t La entidad que se va a guardar.
     * @return Un Mono que emite la entidad guardada.
     */
    Mono<T> save(T t);

    /**
     * Actualiza una entidad en el repositorio.
     *
     * @param t La entidad que se va a actualizar.
     * @return Un Mono que emite la entidad actualizada.
     */
    Mono<T> update(T t);

    /**
     * Busca una entidad por su identificador.
     *
     * @param id El identificador de la entidad que se busca.
     * @return Un Mono que emite la entidad encontrada o un valor vacio si no se encuentra.
     */
    Mono<T> findById(ID id);

    /**
     * Obtiene todas las entidades en el repositorio.
     *
     * @return Un Flux que emite todas las entidades.
     */
    Flux<T> findAll();

    /**
     * Elimina una entidad por su identificador.
     *
     * @param id El identificador de la entidad que se va a eliminar.
     * @return Un Mono que emite `true` si se elimino con exito o `false` si no se encontro la entidad.
     */
    Mono<Boolean> deleteById(ID id);

    /**
     * Elimina todas las entidades en el repositorio.
     *
     * @return Un Mono que indica la finalizacion de la operacion.
     */
    Mono<Void> deleteAll();
}
