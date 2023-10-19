package develop.server.services.services.funkos;


import develop.common.models.Funko;
import develop.server.services.services.cache.Cache;

interface FunkosCache extends Cache<Long, Funko> {
}