package com.batal.demo.camunda;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.contrib.sampler.RuleBasedRoutingSampler;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.semconv.SemanticAttributes;
import io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

@SpringBootApplication
public class ProcessApplication {

    public static void main(String... args) {
        SpringApplication.run(ProcessApplication.class);
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

    @Bean //(name="camundaBpmDataSource")
	public DataSource dataSource(OpenTelemetry openTelemetry) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.type(HikariDataSource.class);
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("");
		DataSource dataSource = dataSourceBuilder.build();
		return JdbcTelemetry.create(openTelemetry).wrap(dataSource);
	}
}