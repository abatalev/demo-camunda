package com.batal.demo.camunda.task1.step1;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Component
public class BackStep1Logic {

    private static Logger log = LoggerFactory.getLogger(BackStep1Logic.class);

    public void execute(ExternalTask task, ExternalTaskService service) {
        log.info("Begin  - id '{}' taskId '{}'", task.getId(), task.getVariable("task_id"));
        try {
            sleep(TimeUnit.SECONDS.toMillis(1));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Finish - id '{}' taskId '{}'", task.getId(), task.getVariable("task_id"));
        service.complete(task);
    }
}
