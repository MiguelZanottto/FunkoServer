package develop.server.services.services.funkos;

import develop.common.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Interfaz que define las operaciones relacionadas con Funko.
 */
public interface FunkosService {
    /**
     * Recupera un Flujo (Flux) de todos los Funko disponibles.
     *
     * @return Flux de Funko que contiene todos los Funkos disponibles.
     */
    Flux<Funko> findAll();

    /**
     * Recupera un Flujo (Flux) de Funko que coinciden con el nombre especificado.
     *
     * @param nombre Nombre por el cual filtrar los Funkos.
     * @return Flux de Funko que contiene los Funkos con el nombre especificado.
     */
    Flux<Funko> findAllByNombre(String nombre);

    /**
     * Busca un Funko por su identificador unico.
     *
     * @param id Identificador unico del Funko.
     * @return Mono que puede contener el Funko correspondiente al identificador o ser vacio si no se encuentra.
     */
    Mono<Funko> findById(long id);

    /**
     * Guarda un nuevo Funko en el sistema.
     *
     * @param funko Funko a ser guardado.
     * @return Mono que contiene el Funko guardado.
     */
    Mono<Funko> save(Funko funko);

    /**
     * Actualiza un Funko existente en el sistema.
     *
     * @param funko Funko con los cambios a ser aplicados.
     * @return Mono que contiene el Funko actualizado.
     */
    Mono<Funko> update(Funko funko);

    /**
     * Elimina un Funko por su identificador unico.
     *
     * @param id Identificador unico del Funko a ser eliminado.
     * @return Mono que indica la finalizacion de la eliminacion.
     */
    Mono<Funko> deleteById(long id);

    /**
     * Elimina todos los Funkos del sistema.
     *
     * @return Mono que indica la finalizacion de la eliminacion de todos los Funkos.
     */
    Mono<Void> deleteAll();

    /**
     * Busca un Funko por su identificador unico (UUID).
     *
     * @param uuid Identificador unico (UUID) del Funko.
     * @return Mono que puede contener el Funko correspondiente al UUID o ser vacio si no se encuentra.
     */
    Mono<Funko> findByUuid(UUID uuid);
}