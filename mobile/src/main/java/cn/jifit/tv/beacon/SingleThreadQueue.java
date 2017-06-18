package cn.jifit.tv.beacon;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by addler on 2017/6/6.
 */

public class SingleThreadQueue {

    //Singleton
    private SingleThreadQueue(){}
    private static class SingletonHelper{
        private static final SingleThreadQueue INSTANCE = new SingleThreadQueue();
    }
    public static SingleThreadQueue getInstance(){
        return SingletonHelper.INSTANCE;
    }

    // Executor Service, Keep Single Thread to avoid thread safe problems
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public <T>Future<T> submit(Callable<T> task){
        return EXECUTOR.submit(task);
    }
    public void shutdown(){
        EXECUTOR.shutdownNow();
    }
}
