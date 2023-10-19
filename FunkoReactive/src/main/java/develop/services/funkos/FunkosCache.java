package develop.services.funkos;


import develop.models.Funko;
import develop.services.cache.Cache;

public interface FunkosCache extends Cache<Long, Funko> {
}