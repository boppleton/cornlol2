package lol.corn.utils;

import org.atmosphere.cache.BroadcastMessage;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broadcaster implements Serializable {

    private static final long serialVersionUID = 3540459607283346649L;

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

    public interface BroadcastListener {
        void receiveBroadcast(BroadcastMessage message);
    }   

    public static synchronized void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    public static synchronized void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcast(final BroadcastMessage message) {
        for (final BroadcastListener listener: listeners)
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    listener.receiveBroadcast(message);
                }
            });
    }
}