package com.batal.demo.camunda.task1.step1;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import org.camunda.bpm.client.spring.SpringTopicSubscription;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static io.opentelemetry.api.trace.StatusCode.ERROR;

@Component
@Qualifier("step1")
@ExternalTaskSubscription(topicName = "step1")
public class Step1Handler implements ExternalTaskHandler {

    private static Logger log = LoggerFactory.getLogger(Step1Handler.class);

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private SpringTopicSubscription subscription;

    private Step1Logic logic;
    private final OpenTelemetry openTelemetry;

    @Autowired
    public Step1Handler(@Lazy @Qualifier("Step1HandlerSubscription") SpringTopicSubscription subscription, 
                        Step1Logic logic, OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
        this.subscription = subscription;
        this.logic = logic;
    }

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        Tracer tracer = openTelemetry.getTracer(Step1Handler.class.getName(), "1");
        Span span = tracer.spanBuilder("step1-submit").startSpan();
        try {
            try (Scope ignored = span.makeCurrent()) {
                span.setAttribute("id", task.getId());

                log.info("Submit - id '{}'", task.getId());
                executor.submit(() -> runLogic(task, service));
                if (((ThreadPoolExecutor) executor).getQueue().size() > 9) {
                    log.info("Close");
                    subscription.close();
                }
            } catch (RuntimeException e) {
                log.error("error: " + e.getMessage());
                span.setStatus(ERROR);
                span.setAttribute("result", "error," + e.getMessage());
            }
        } finally {
            span.end();
        }
    }

    private void runLogic(ExternalTask task, ExternalTaskService service) {
        Tracer tracer = openTelemetry.getTracer(BackStep1Handler.class.getName(), "1");
        Span span = tracer.spanBuilder("step1-logic").startSpan();
        try {
            try (Scope ignored = span.makeCurrent()) {
                span.setAttribute("id", task.getId());

                logic.execute(task, service, span);

                span.setAttribute("result", "ok");
            } catch (RuntimeException e) {
                log.error("error: " + e.getMessage());
                span.setStatus(ERROR);
                span.setAttribute("result", "error," + e.getMessage());
            }
            if (((ThreadPoolExecutor) executor).getQueue().size() < 2) {
                if (!subscription.isOpen()) {
                    log.info("Open");
                    subscription.open();
                }
            }
        } finally {
            span.end();
        }
    }
}
