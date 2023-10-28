package develop.server.services.services.funkos;

import develop.common.models.Funko;
import develop.common.models.Notificacion;
import reactor.core.publisher.Flux;

/**
 * Interfaz que define operaciones para notificar eventos relacionados con Funko.
 */
public interface FunkosNotification {
    /**
     * Obtiene las notificaciones como un Flujo (Flux) de Notificaciones relacionados con Funko.
     *
     * @return Flux de Notificacion<Funko> que proporciona notificaciones de eventos relacionados con Funko.
     */
    Flux<Notificacion<Funko>> getNotificationAsFlux();

    /**
     * Notifica un evento relacionado con Funko a traves de una Notificacion.
     *
     * @param notificacion Notificacion que describe el evento ocurrido.
     */
    void notify(Notificacion<Funko> notificacion);
}
