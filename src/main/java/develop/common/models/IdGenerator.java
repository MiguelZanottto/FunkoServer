package develop.common.models;


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
        locker.lock();
        id++;
        locker.unlock();
        return id;
    }

    public void resetId() {
        id = 0L;
    }
}

