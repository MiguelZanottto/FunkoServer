package develop.common.models;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Clase que implementa un generador de identificadores unicos para los Funkos.
 */
public class IdGenerator {
    private static IdGenerator instance;
    private static long id = 0;
    private final Lock locker = new ReentrantLock(true);

    /**
     * Metodo estatico que devuelve una instancia del generador de identificadores.
     * Si la instancia aun no existe, se crea una nueva.
     *
     * @return Instancia unica del generador de identificadores.
     */
    public static synchronized IdGenerator getInstance(){
        if(instance == null){
            instance = new IdGenerator();
        }
        return instance;
    }
    /**
     * Genera un nuevo identificador unico y lo incrementa en uno.
     *
     * @return El nuevo identificador unico generado.
     */
    public Long getIdAndIncrement() {
        locker.lock();
        id++;
        locker.unlock();
        return id;
    }
    /**
     * Restablece el identificador a su valor inicial (cero).
     */
    public void resetId() {
        id = 0L;
    }
}

