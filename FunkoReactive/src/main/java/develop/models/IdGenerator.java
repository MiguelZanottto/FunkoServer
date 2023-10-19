package develop.models;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class IdGenerator {
    private static IdGenerator instance;
    private static long id = 0;
    private final Lock locker = new ReentrantLock(true);


    public static synchronized IdGenerator getInstance(){
        if(instance == null){
            instance = new IdGenerator();
        }
        return instance;
    }

    public Long getIdAndIncrement() {
        Long idCopia = 0L;
        locker.lock();
        id++;
        idCopia = id;
        locker.unlock();
        return idCopia;
    }

    public void resetId() {
        this.id = 0L;
    }
}

