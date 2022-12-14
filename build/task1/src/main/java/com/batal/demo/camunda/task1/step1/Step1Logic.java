package com.batal.demo.camunda.task1.step1;

import io.opentracing.Span;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.Thread.sleep;

@Component
public class Step1Logic {

    private static Logger log = LoggerFactory.getLogger(Step1Logic.class);

    public void execute(ExternalTask task, ExternalTaskService service, Span span) {
        String taskId = task.getVariable("task_id");
        span.setTag("task-id",taskId);
        log.info("Begin  - id '{}' taskId '{}'", task.getId(), taskId);
        Map<String, Object> vars = new HashMap<>();
        long rnd = round(20 * random());
        String val = rnd > 10 ? "error" : "ok";
        vars.put("result", val);
        try {
            sleep(TimeUnit.SECONDS.toMillis(rnd));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Finish - id '{}' taskId '{}' result '{}'", task.getId(), taskId, val);
        span.setTag("logic-result", val);
        service.complete(task, vars);
    }
}
