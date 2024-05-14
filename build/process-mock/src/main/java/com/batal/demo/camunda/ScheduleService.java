package com.batal.demo.camunda;

import io.micrometer.core.annotation.Timed;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static io.opentelemetry.api.trace.StatusCode.ERROR;

// https://opentelemetry.io/docs/languages/java/instrumentation/
// https://github.com/open-telemetry/opentelemetry-java-examples/tree/main/jaeger
// https://github.com/open-telemetry/opentelemetry-java-examples/tree/main

@Service
@Timed
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public ScheduleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelayString = "1000")
    @Timed
    public void run() {
        Tracer tracer = GlobalOpenTelemetry.getTracer(ScheduleService.class.getName(), "1");
        Span span = tracer.spanBuilder("run").startSpan();
        try {
            try (Scope ignored = span.makeCurrent()) {
                String answer = restTemplate.getForObject("http://process:8080/engine-rest/process-definition/", String.class);
                log.info("Answer: " + answer);
                String object = restTemplate.postForObject("http://process:8080/engine-rest/process-definition/key/process1-process/start",
                        new HashMap<>(), String.class);
                log.info(object);

                span.setAttribute("result", object);
            } catch (RuntimeException e) {
                log.error("error: " + e.getMessage());
                span.setStatus(ERROR);
                span.recordException(e);
                span.setAttribute("result", "error," + e.getMessage());
            }
        } finally {
            span.end();
        }
    }
}
