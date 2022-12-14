package com.batal.demo.camunda;

import io.micrometer.core.annotation.Timed;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
@Timed
public class ScheduleService {

    private static Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private RestTemplate restTemplate;

    @Autowired
    public ScheduleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelayString = "1000")
    @Timed
    public void run() {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("run").start();
        try {
            try (Scope ignored = tracer.activateSpan(span)) {

                String answer = restTemplate.getForObject("http://process:8080/engine-rest/process-definition/", String.class);
                log.info("Answer: " + answer);
                String object = restTemplate.postForObject("http://process:8080/engine-rest/process-definition/key/process1-process/start",
                        new HashMap(), String.class);
                log.info(object);

                span.setTag("result", object);
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
