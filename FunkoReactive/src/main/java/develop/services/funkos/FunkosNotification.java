package develop.services.funkos;

import develop.models.Funko;
import develop.models.Notificacion;
import reactor.core.publisher.Flux;

public interface FunkosNotification {
    Flux<Notificacion<Funko>> getNotificationAsFlux();

    void notify(Notificacion<Funko> notificacion);
}