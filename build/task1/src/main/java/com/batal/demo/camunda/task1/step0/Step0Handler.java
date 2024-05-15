package com.batal.demo.camunda.task1.step0;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.opentelemetry.api.trace.StatusCode.ERROR;

@Component
@ExternalTaskSubscription(topicName = "step0")
public class Step0Handler implements ExternalTaskHandler {

    private final static Logger log = LoggerFactory.getLogger(Step0Handler.class);
    private final OpenTelemetry openTelemetry;

    @Autowired
    public Step0Handler(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {

        Tracer tracer = openTelemetry.getTracer(Step0Handler.class.getName(), "1");
        Span span = tracer.spanBuilder("step0").startSpan();
        try {
            try (Scope ignored = span.makeCurrent()) {
                span.setAttribute("id", task.getId());

                String taskId = UUID.randomUUID().toString();
                log.info("Finish - id '{}' taskId '{}'", task.getId(), taskId);
                Map<String, Object> vars = new HashMap<>();
                vars.put("task_id", taskId);
                span.setAttribute("task-id", taskId);
                service.complete(task, vars);

                span.setAttribute("result", "ok");
            } catch (RuntimeException e) {
                log.error("error: " + e.getMessage());
                span.setStatus(ERROR);
                span.setAttribute("result", "error," + e.getMessage());
            }
        } finally {
            span.end();
        }
    }
}
