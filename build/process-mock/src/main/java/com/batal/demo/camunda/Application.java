package com.batal.demo.camunda;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.web.v3_1.SpringWebTelemetry;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.contrib.sampler.RuleBasedRoutingSampler;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.semconv.SemanticAttributes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

    @Bean
    public AutoConfigurationCustomizerProvider otelCustomizer() {
      return p ->
          p.addSamplerCustomizer(
              (fallback, config) ->
                  RuleBasedRoutingSampler.builder(SpanKind.SERVER, fallback)
                      .drop(SemanticAttributes.URL_PATH, "^/actuator")
                      .build());
    }
}
