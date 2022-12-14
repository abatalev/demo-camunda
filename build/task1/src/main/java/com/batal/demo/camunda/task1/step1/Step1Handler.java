package com.batal.demo.camunda.task1.step1;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
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

@Component
@Qualifier("step1")
@ExternalTaskSubscription(topicName = "step1")
public class Step1Handler implements ExternalTaskHandler {

    private static Logger log = LoggerFactory.getLogger(Step1Handler.class);

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private SpringTopicSubscription subscription;

    private Step1Logic logic;

    @Autowired
    public Step1Handler(@Lazy @Qualifier("Step1HandlerSubscription") SpringTopicSubscription subscription, Step1Logic logic) {
        this.subscription = subscription;
        this.logic = logic;
    }

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        log.info("Submit - id '{}'", task.getId());
        executor.submit(() -> runLogic(task, service));
        if (((ThreadPoolExecutor) executor).getQueue().size() > 9) {
            log.info("Close");
            subscription.close();
        }
    }

    private void runLogic(ExternalTask task, ExternalTaskService service) {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("step1").start();
        try {
            try (Scope ignored = tracer.activateSpan(span)) {
                span.setTag("id", task.getId());

                logic.execute(task, service, span);

                span.setTag("result", "ok");
            } catch (RuntimeException e) {
                log.error("error: " + e.getMessage());
                span.setTag("error", "true");
                span.setTag("result", "error," + e.getMessage());
            }
        } finally {
            span.finish();
            if (((ThreadPoolExecutor) executor).getQueue().size() < 2) {
                if (!subscription.isOpen()) {
                    log.info("Open");
                    subscription.open();
                }
            }
        }
    }
}
