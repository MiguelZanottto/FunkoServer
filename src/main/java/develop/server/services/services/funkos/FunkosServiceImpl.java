package develop.server.services.services.funkos;

import develop.common.models.Funko;
import develop.common.models.Notificacion;
import develop.server.exceptions.funkos.FunkoNoEncotradoException;
import develop.server.repositories.funkos.FunkosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public class FunkosServiceImpl implements FunkosService {
    private static final int CACHE_SIZE = 10;
    private static FunkosServiceImpl instance;
    private final FunkosCache cache;
    private final FunkosNotification notification;
    private final Logger logger = LoggerFactory.getLogger(FunkosServiceImpl.class);
    private final FunkosRepository funkosRepository;
    private final FunkosStorage storage;

    private FunkosServiceImpl(FunkosRepository funkosRepository, FunkosNotification notification, FunkosStorage storage) {
        this.funkosRepository = funkosRepository;
        this.cache = new FunkosCacheImpl(CACHE_SIZE);
        this.notification = notification;
        this.storage = storage;
    }


    public static FunkosServiceImpl getInstance(FunkosRepository funkosRepository, FunkosNotification notification, FunkosStorage storage) {
        if (instance == null) {
            instance = new FunkosServiceImpl(funkosRepository, notification, storage);
        }
        return instance;
    }
    /**
     * Recupera un Flux de todos los Funkos disponibles en el sistema.
     *
     * @return Flux que emite todos los Funkos existentes.
     */
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los funkos");
        return funkosRepository.findAll();
    }

    /**
     * Recupera un Flux de Funkos que coinciden con un nombre especifico.
     *
     * @param nombre Nombre de Funko para realizar la busqueda.
     * @return Flux que emite Funkos con el nombre especificado.
     */
    @Override
    public Flux<Funko> findAllByNombre(String nombre) {
        logger.debug("Buscando todos los funkos por nombre");
        return funkosRepository.findByNombre(nombre);
    }

    /**
     * Busca y recupera un Funko por su identificador unico.
     *
     * @param id Identificador unico del Funko a buscar.
     * @return Mono que puede contener el Funko correspondiente al identificador o estar vacio si no se encuentra.
     */
    @Override
    public Mono<Funko> findById(long id) {
        logger.debug("Buscando funko por id: " + id);
        return cache.get(id)
                .switchIfEmpty(funkosRepository.findById(id)
                        .flatMap(funko -> cache.put(funko.getId(), funko)
                                .then(Mono.just(funko)))
                        .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con id " + id + " no encontrado"))));
    }

    /**
     * Busca y recupera un Funko por su identificador unico (UUID).
     *
     * @param uuid UUID del Funko a buscar.
     * @return Mono que puede contener el Funko correspondiente al UUID o estar vacio si no se encuentra.
     */
    public Mono<Funko> findByUuid(UUID uuid) {
        logger.debug("Buscando funko por uuid: " + uuid);
        return funkosRepository.findByUuid(uuid)
                .flatMap(funko -> cache.put(funko.getId(), funko)
                        .then(Mono.just(funko)))
                .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con uuid " + uuid + " no encontrado")));
    }

    /**
     * Guarda un Funko en el sistema sin generar notificaciones.
     *
     * @param funko Funko a ser guardado.
     * @return Mono que contiene el Funko guardado.
     */
    public Mono<Funko> saveWithoutNotification(Funko funko) {
        logger.debug("Guardando funko sin notificación: " + funko);
        return funkosRepository.save(funko)
                .flatMap(saved -> findByUuid(saved.getCod()));
    }

    /**
     * Guarda un Funko en el sistema y genera una notificacion de nuevo Funko en el sitema.
     * @param funko Funko a ser guardado.
     * @return Mono que contiene el Funko guardado
     */
    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando funko: " + funko);
        return saveWithoutNotification(funko)
                .doOnSuccess(saved -> notification.notify(new Notificacion<>(Notificacion.Tipo.NEW, saved)));
    }

    /**
     * Actualiza un Funko existente en el sistema sin generar una notificacion
     * @param funko Funko con los cambios a ser aplicados
     * @return Mono que contiene el Funko actualizado
     */
    private Mono<Funko> updateWithoutNotification(Funko funko) {
        logger.debug("Actualizando funko sin notificación: " + funko);
        return funkosRepository.findById(funko.getId())
                .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con id " + funko.getId() + " no encontrado")))
                .flatMap(existing -> funkosRepository.update(funko)
                        .flatMap(updated -> cache.put(updated.getId(), updated)
                                .thenReturn(updated)));
    }

    /**
     * Actualiza un Funko existente en el sistema y genera una notificacion
     * @param funko Funko con los cambios a ser aplicados.
     * @return Mono que contiene el Funko actualizado
     */
    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
        return updateWithoutNotification(funko)
                .doOnSuccess(updated -> notification.notify(new Notificacion<>(Notificacion.Tipo.UPDATED, updated)));
    }
    /**
     * Borra un Funko del sistema sin generar notificaciones
     * @param id Identificador unico del Funko a ser eliminado.
     * @return Mono que contiene el Funko eliminado
     */
    private Mono<Funko> deleteByIdWithoutNotification(long id) {
        logger.debug("Borrando funko sin notificación con id: " + id);
        return funkosRepository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con id " + id + " no encontrado")))
                .flatMap(funko -> cache.remove(funko.getId())
                        .then(funkosRepository.deleteById(funko.getId()))
                        .thenReturn(funko));
    }

    /**
     * Borra un Funko con el identificador funko y genera una notificacion de eliminacion
     *
     * @param id Identificador unico del Funko a ser eliminado.
     * @return Mono que contiene el Funko eliminado.
     */
    @Override
    public Mono<Funko> deleteById(long id) {
        logger.debug("Borrando funko por id: " + id);
        return deleteByIdWithoutNotification(id)
                .doOnSuccess(deleted -> notification.notify(new Notificacion<>(Notificacion.Tipo.DELETED, deleted)));
    }
    /**
     * Importa una lista de Funos desde un archivo CSV.
     * @return Flux que contiene los Funkos importados
     */
    public Flux<Funko> importFile() {
        logger.debug("Importando lista de funkos desde csv");
        return storage.importCsv();
    }

    /**
     * Elimina todos los Funkos del sistema y borra la cache
     * @return Mono que indica la finalizacion de la eliminacion de todos los funkos
     */

    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Borrando todos los funkos");
        cache.clear();
        return funkosRepository.deleteAll()
                .then(Mono.empty());
    }
    /**
     * Obtiene un Flujo (Flux) de notificaciones de Funkos.
     *
     * @return Flux que emite notificaciones de cambios en Funkos.
     */
    public Flux<Notificacion<Funko>> getNotifications() {
        return notification.getNotificationAsFlux();
    }
}