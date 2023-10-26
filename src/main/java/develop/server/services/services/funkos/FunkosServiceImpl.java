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

    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los funkos");
        return funkosRepository.findAll();
    }

    @Override
    public Flux<Funko> findAllByNombre(String nombre) {
        logger.debug("Buscando todos los funkos por nombre");
        return funkosRepository.findByNombre(nombre);
    }

    @Override
    public Mono<Funko> findById(long id) {
        logger.debug("Buscando funko por id: " + id);
        return cache.get(id)
                .switchIfEmpty(funkosRepository.findById(id)
                        .flatMap(funko -> cache.put(funko.getId(), funko)
                                .then(Mono.just(funko)))
                        .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con id " + id + " no encontrado"))));
    }


    public Mono<Funko> findByUuid(UUID uuid) {
        logger.debug("Buscando funko por uuid: " + uuid);
        return funkosRepository.findByUuid(uuid)
                .flatMap(funko -> cache.put(funko.getId(), funko)
                        .then(Mono.just(funko)))
                .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con uuid " + uuid + " no encontrado")));
    }


    public Mono<Funko> saveWithoutNotification(Funko funko) {
        logger.debug("Guardando funko sin notificación: " + funko);
        return funkosRepository.save(funko)
                .flatMap(saved -> findByUuid(saved.getCod()));
    }

    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando funko: " + funko);
        return saveWithoutNotification(funko)
                .doOnSuccess(saved -> notification.notify(new Notificacion<>(Notificacion.Tipo.NEW, saved)));
    }

    private Mono<Funko> updateWithoutNotification(Funko funko) {
        logger.debug("Actualizando funko sin notificación: " + funko);
        return funkosRepository.findById(funko.getId())
                .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con id " + funko.getId() + " no encontrado")))
                .flatMap(existing -> funkosRepository.update(funko)
                        .flatMap(updated -> cache.put(updated.getId(), updated)
                                .thenReturn(updated)));
    }


    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
        return updateWithoutNotification(funko)
                .doOnSuccess(updated -> notification.notify(new Notificacion<>(Notificacion.Tipo.UPDATED, updated)));
    }

    private Mono<Funko> deleteByIdWithoutNotification(long id) {
        logger.debug("Borrando funko sin notificación con id: " + id);
        return funkosRepository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNoEncotradoException("Funko con id " + id + " no encontrado")))
                .flatMap(funko -> cache.remove(funko.getId())
                        .then(funkosRepository.deleteById(funko.getId()))
                        .thenReturn(funko));
    }

    @Override
    public Mono<Funko> deleteById(long id) {
        logger.debug("Borrando funko por id: " + id);
        return deleteByIdWithoutNotification(id)
                .doOnSuccess(deleted -> notification.notify(new Notificacion<>(Notificacion.Tipo.DELETED, deleted)));
    }

    public Flux<Funko> importFile() {
        logger.debug("Importando lista de funkos desde csv");
        return storage.importCsv();
    }

    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Borrando todos los funkos");
        cache.clear();
        return funkosRepository.deleteAll()
                .then(Mono.empty());
    }

    public Flux<Notificacion<Funko>> getNotifications() {
        return notification.getNotificationAsFlux();
    }
}