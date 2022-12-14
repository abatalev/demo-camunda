# Demo Embedded Camunda

## Build 

```sh
./mk.sh
```

## Process

![](process.png)

## Jaeger

![](jaeger.png)

## Endpoints 

- Prometheus - http://localhost:9090/
- Jaeger - http://localhost:16686/
- process-mock -  http://localhost:8082/
- process - http://localhost:8081/engine-rest/process-definition/

## See also

- https://reflectoring.io/spring-boot-tracing/
- https://camunda.com/blog/2022/10/monitoring-camunda-platform-7-with-prometheus/

- Тестирование -- https://habr.com/ru/company/simbirsoft/blog/657697/

- https://blog.viadee.de/en/camunda-external-tasks-error-handling-and-retry-behavior
- https://blog.viadee.de/en/camunda-external-task-worker

- https://docs.camunda.io/docs/components/best-practices/development/invoking-services-from-the-process-c7/
- https://github.com/camunda-consulting/camunda-7-code-examples/tree/master/snippets

- https://camunda.com/blog/2022/02/moving-from-embedded-to-remote-workflow-engines/
- https://github.com/camunda/camunda-bpm-examples/tree/master/spring-boot-starter/external-task-client/request-interceptor-spring-boot

- https://github.com/camunda/camunda-bpm-examples/blob/master/spring-boot-starter/external-task-client/order-handling-spring-boot/src/main/java/org/camunda/bpm/spring/boot/example/Subscriptions.java