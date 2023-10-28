package develop.common.models;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa una notificacion.
 *
 * @param <T> Tipo del contenido de la notificación.
 */
@Getter
@Setter
public class Notificacion<T> {
    private Tipo tipo; //Tipo de notificacion
    private T contenido;//Contenido de la notificacion

    /**
     * Se crea un constructor de Notificacion con el tipo y contenido
     * @param tipo El tipo de notificacion que se recibira
     * @param contenido El contenido de la notificacion
     */
    public Notificacion(Tipo tipo, T contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    /**
     * Devuelve una represantacion en cadena de la notificacion
     * @return Una cadena que muestra el tipo y el contenido de la notificacion
     */
    @Override
    public String toString() {
        return "Notificacion{" +
                "tipo=" + tipo +
                ", contenido=" + contenido +
                '}';
    }

    /**
     * Y enumeracion que define los tipos de notificaciones, NEW, UPDATED Y DELETED.
     */
    public enum Tipo {
        NEW, UPDATED, DELETED
    }
}