package org.orlo.task.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TaskConfig {
    ExecutorService pool= Executors.newCachedThreadPool();
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

    public static final String CONTROLLER_IP = "192.168.5.137";
    public static final int CONTROLLER_PORT = 1060;
}


