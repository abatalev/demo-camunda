package com.batal.demo.camunda.task1;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.contrib.sampler.RuleBasedRoutingSampler;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.semconv.SemanticAttributes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Task1Application {
    public static void main(String... args) {
        SpringApplication.run(Task1Application.class);
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