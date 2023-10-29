package develop.server.services.services.funkos;

import develop.common.models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implentacion de la interfaz FunkosCache que almacena Funkos en un cache limitada de eliminacion por caducidad y un tamano maximo
 */
public class FunkosCacheImpl implements FunkosCache {
    private final Logger logger = LoggerFactory.getLogger(FunkosCacheImpl.class);
    private final int maxSize;
    private final Map<Long, Funko> cache;
    private final ScheduledExecutorService cleaner;

    /**
     * Constructor de FunkosCacheImpl.
     *
     * @param maxSize Tamano maximo de la cache.
     */
    public FunkosCacheImpl(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<Long, Funko>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Funko> eldest) {
                return size() > maxSize;
            }
        };
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear, 2, 2, TimeUnit.MINUTES);
    }
    /**
     * Agrega un Funko a la cache.
     *
     * @param key   Identificador unico del Funko.
     * @param value Objeto Funko a almacenar en la cache.
     * @return Mono<Void> que indica si la operacion se realizo con exito.
     */
    @Override
    public Mono<Void> put(Long key, Funko value) {
        logger.debug("Añadiendo funko a cache con id: " + key + " y valor: " + value);
        return Mono.fromRunnable(() -> cache.put(key, value));
    }

    /**
     * Obtiene un Funko de la cache.
     *
     * @param key Identificador unico del Funko.
     * @return Mono que puede contener el Funko solicitado o estar vacio si no se encuentra en la cache.
     */
    @Override
    public Mono<Funko> get(Long key) {
        logger.debug("Obteniendo funko de cache con id: " + key);
        return Mono.justOrEmpty(cache.get(key));
    }

    /**
     * Elimina un Funko de la cache
     * @param key Identificador que se desea eliminar el valor.
     * @return Mono<Void> que indica si la operacion se realizo con exito
     */
    @Override
    public Mono<Void> remove(Long key) {
        logger.debug("Eliminando funko de cache con id: " + key);
        return Mono.fromRunnable(() -> cache.remove(key));
    }

    /**
     * Limpia el cache de los Funkos caducados.
     */
    @Override
    public void clear() {
        cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdatedAt().plusMinutes(2).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.debug("Autoeliminando por caducidad funko de cache con id: " + entry.getKey());
            }
            return shouldRemove;
        });
    }

    /**
     * Detiene el limpiador de cache
     */
    @Override
    public void shutdown() {
        cleaner.shutdown();
    }

    /**
     * Obtiene el tamano de la cache
     * @return Tamano actual de la cache
     */
    public int getTamano(){
        return this.cache.size();
    }
}