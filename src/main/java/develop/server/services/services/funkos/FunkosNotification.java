package develop.server.services.services.funkos;

import develop.common.models.Funko;
import develop.common.models.Notificacion;
import reactor.core.publisher.Flux;

public interface FunkosNotification {
    Flux<Notificacion<Funko>> getNotificationAsFlux();

    void notify(Notificacion<Funko> notificacion);
}