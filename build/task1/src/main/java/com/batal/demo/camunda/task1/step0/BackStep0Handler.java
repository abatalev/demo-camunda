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

@Component
@ExternalTaskSubscription(topicName = "backStep0")
public class BackStep0Handler implements ExternalTaskHandler {

    private static Logger log = LoggerFactory.getLogger(BackStep0Handler.class);

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {

        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("backStep0").start();
        try {
            try (Scope ignored = tracer.activateSpan(span)) {
                span.setTag("id", task.getId());
                span.setTag("task-id", (String) task.getVariable("task_id"));

                log.info("Finish - id '{}' taskId '{}'", task.getId(), task.getVariable("task_id"));
                service.complete(task);

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
