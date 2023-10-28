package develop.server.services.services.cache;

import reactor.core.publisher.Mono;

/**
 * Interfaz que define una cache para almacenar y recuperar: claves y valores
 *
 * @param <K> Tipo de la clave.
 * @param <V> Tipo del valor almacenado en cache.
 */
public interface Cache<K, V> {
    /**
     * Almacena un valor en la cache asociandolo a una clave.
     *
     * @param key   Clave del cual se almacena el valor.
     * @param value Valor a ser almacenado en cache.
     * @return Un Mono que completa sin un valor cuando la operacion ha sido completada.
     */
    Mono<Void> put(K key, V value);

    /**
     * Recupera un valor almacenado en cache asociado a una clave.
     *
     * @param key Clave que desea recuperar el valor.
     * @return Un Mono que emite el valor a la clave, o vacio si no existe.
     */
    Mono<V> get(K key);

    /**
     * Elimina un valor almacenado en cache asociado a una clave.
     *
     * @param key Clave que se desea eliminar el valor.
     * @return Un Mono que completa sin un valor cuando la operacion ha sido completada.
     */
    Mono<Void> remove(K key);

    /**
     * Elimina todos los valores almacenados en la cache.
     */
    void clear();

    /**
     * Realiza cualquier limpieza o liberacion necesaria al apagar la cache.
     */
    void shutdown();
}
