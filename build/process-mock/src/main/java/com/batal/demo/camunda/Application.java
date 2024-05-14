package com.batal.demo.camunda;


import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.web.v3_1.SpringWebTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public RestTemplate restTemplate(OpenTelemetry openTelemetry) {
        RestTemplate restTemplate = new RestTemplate();
        SpringWebTelemetry telemetry = SpringWebTelemetry.create(openTelemetry);
        restTemplate.getInterceptors().add(telemetry.newInterceptor());
        return restTemplate;
    }
}
