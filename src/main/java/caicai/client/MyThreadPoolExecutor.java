package caicai.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutor  {
    private MyThreadPoolExecutor(){}
    private static  ThreadPoolExecutor instance;
    public static ThreadPoolExecutor getInstance(){
        if (instance==null){
            synchronized (MyThreadPoolExecutor.class){
                if (instance==null){
                instance= new ThreadPoolExecutor(16, 16,
                        600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
            }
        }
    }
        return instance;
    }



}
