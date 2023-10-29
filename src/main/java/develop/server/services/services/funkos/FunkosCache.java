package develop.server.services.services.funkos;


import develop.common.models.Funko;
import develop.server.services.services.cache.Cache;

/**
 * Interfaz que define metodos para cachear objetos de Funko utilizando el cache.
 * Esta interfaz extiende la interfaz Cache y esta disenada especificamente para cachear Funko
 * por su identificador unico (ID).*
 * por su identificador unico (ID).*
 */
interface FunkosCache extends Cache<Long, Funko> {
}
