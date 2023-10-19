package develop.common.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notificacion<T> {
    private Tipo tipo;
    private T contenido;
    public Notificacion(Tipo tipo, T contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "tipo=" + tipo +
                ", contenido=" + contenido +
                '}';
    }

    public enum Tipo {
        NEW, UPDATED, DELETED
    }
}