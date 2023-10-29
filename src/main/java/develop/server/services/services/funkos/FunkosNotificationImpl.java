package develop.server.services.services.funkos;


import develop.common.models.Funko;
import develop.common.models.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * Implementacion de la interfaz FunkosNotification que permite notificar eventos relacionados con objetos Funko.
 */
public class FunkosNotificationImpl implements FunkosNotification {
    private static FunkosNotificationImpl instance = new FunkosNotificationImpl();

    private final Flux<Notificacion<Funko>> funkosNotificationFlux;

    private FluxSink<Notificacion<Funko>> funkosNotification;

    /**
     * Constructor privado para crear una instancia de FunkosNotificationImpl.
     */
    private FunkosNotificationImpl() {
        this.funkosNotificationFlux = Flux.<Notificacion<Funko>>create(emitter -> this.funkosNotification = emitter).share();
    }

    /**
     * Obtiene una instancia de FunkosNotificationImpl (singleton).
     *
     * @return Instancia de FunkosNotificationImpl.
     */
    public static FunkosNotificationImpl getInstance() {
        if (instance == null) {
            instance = new FunkosNotificationImpl();
        }
        return instance;
    }

    /**
     * Obtiene un Flujo (Flux) de Notificaciones relacionadas con Funko.
     *
     * @return Flux de Notificacion<Funko> que proporciona notificaciones de eventos relacionados con Funko.
     */
    @Override
    public Flux<Notificacion<Funko>> getNotificationAsFlux() {
        return funkosNotificationFlux;
    }

    /**
     * Notifica un evento relacionado con Funko a traves de una Notificacion.
     *
     * @param notificacion Notificacion que describe el evento ocurrido.
     */
    @Override
    public void notify(Notificacion<Funko> notificacion) {
        funkosNotification.next(notificacion);
    }
}
