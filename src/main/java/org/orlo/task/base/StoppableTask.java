package org.orlo.task.base;

public interface StoppableTask extends Runnable {
    void start();
    void stop();
}
