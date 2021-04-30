package org.orlo.task.base;

import java.util.concurrent.*;

public class TaskConfig {
    ExecutorService pool= new ThreadPoolExecutor(10, 20, 60,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    ScheduledExecutorService scheduledPool= Executors.newScheduledThreadPool(10);
    static TaskConfig instance=new TaskConfig();
    private TaskConfig(){}
    public static TaskConfig getInstance(){
        return instance;
    }
    public ExecutorService getPool(){
        return pool;
    }
    public ScheduledExecutorService getScheduledPool(){
        return scheduledPool;
    }
    //控制器
    public static final String CONTROLLER_IP = "172.16.64.128";
    public static final int CONTROLLER_PORT = 1060;


    //计算信任度的服务
    public static final String LOF_IP = "127.0.0.1";
    public static final int LOF_PORT = 1062;

}


