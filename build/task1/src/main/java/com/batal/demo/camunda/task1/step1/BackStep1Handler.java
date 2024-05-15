package com.batal.demo.camunda.task1.step1;

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

import static io.opentelemetry.api.trace.StatusCode.ERROR;

@Component
@ExternalTaskSubscription(topicName = "backStep1")
public class BackStep1Handler implements ExternalTaskHandler {

    private final static Logger log = LoggerFactory.getLogger(BackStep1Handler.class);
    private final OpenTelemetry openTelemetry;
    private final BackStep1Logic logic;

    @Autowired
    public BackStep1Handler(BackStep1Logic logic,OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
        this.logic = logic;
    }

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        Tracer tracer = openTelemetry.getTracer(BackStep1Handler.class.getName(), "1");
        Span span = tracer.spanBuilder("backStep1").startSpan();
        try {
            try (Scope ignored = span.makeCurrent()) {
                span.setAttribute("id", task.getId());
                span.setAttribute("task-id", (String) task.getVariable("task_id"));
                logic.execute(task, service);
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
