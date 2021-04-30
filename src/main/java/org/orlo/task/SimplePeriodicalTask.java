package org.orlo.task;

import org.orlo.task.base.PeriodicalTask;

public class SimplePeriodicalTask extends PeriodicalTask {
    public interface SimpleTask {
        void task();
    }

    public SimplePeriodicalTask(SimpleTask simpleTask) {
        this.worker = simpleTask::task;
    }
}
