package com.batal.demo.camunda.task1.step0;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@ExternalTaskSubscription(topicName = "step0")
public class Step0Handler implements ExternalTaskHandler {

    private static Logger log = LoggerFactory.getLogger(Step0Handler.class);

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {

        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("step0").start();
        try {
            try (Scope ignored = tracer.activateSpan(span)) {
                span.setTag("id", task.getId());

                String taskId = UUID.randomUUID().toString();
                log.info("Finish - id '{}' taskId '{}'", task.getId(), taskId);
                Map<String, Object> vars = new HashMap<>();
                vars.put("task_id", taskId);
                span.setTag("task-id", taskId);
                service.complete(task, vars);

                span.setTag("result", "ok");
            } catch (RuntimeException e) {
                log.error("error: " + e.getMessage());
                span.setTag("error", "true");
                span.setTag("result", "error," + e.getMessage());
            }
        } finally {
            span.finish();
        }
    }
}
