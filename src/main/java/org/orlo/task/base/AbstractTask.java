package org.orlo.task.base;


import java.util.concurrent.ExecutorService;

public abstract class AbstractTask implements Task {
    protected ExecutorService service= TaskConfig.getInstance().getPool();
    protected Thread worker;
    public void start(){
        if(null==service){
            worker=new Thread(this);
            worker.start();
        }else{
            service.submit(this);
        }
    }
}
