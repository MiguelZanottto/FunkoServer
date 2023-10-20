package develop.server.services.services.funkos;


import develop.common.models.Funko;
import develop.common.models.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class FunkosNotificationImpl implements FunkosNotification {
    private static FunkosNotificationImpl instance = new FunkosNotificationImpl();

    private final Flux<Notificacion<Funko>> funkosNotificationFlux;

    private FluxSink<Notificacion<Funko>> funkosNotification;

    private FunkosNotificationImpl() {
        this.funkosNotificationFlux = Flux.<Notificacion<Funko>>create(emitter -> this.funkosNotification = emitter).share();
    }

    public static FunkosNotificationImpl getInstance() {
        if (instance == null) {
            instance = new FunkosNotificationImpl();
        }
        return instance;
    }

    @Override
    public Flux<Notificacion<Funko>> getNotificationAsFlux() {
        return funkosNotificationFlux;
    }

    @Override
    public void notify(Notificacion<Funko> notificacion) {
        funkosNotification.next(notificacion);
    }
}