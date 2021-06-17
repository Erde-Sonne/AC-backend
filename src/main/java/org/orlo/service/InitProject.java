package org.orlo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitProject implements ApplicationRunner {
    @Autowired
    PeriodTaskService periodTaskService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        periodTaskService.runLOF();
//        periodTaskService.checkConfidence();
    }
}
