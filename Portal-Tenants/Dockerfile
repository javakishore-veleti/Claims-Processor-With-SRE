FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="healthcare-claims-team"
LABEL project="claims-processor-with-sre"
LABEL module="portal-tenants"

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY target/*.jar app.jar

# AWS ADOT Java auto-instrumentation agent (activated via JAVA_TOOL_OPTIONS env var in Fargate)
RUN wget -q -O /opt/aws-opentelemetry-agent.jar \
    https://github.com/aws-observability/aws-otel-java-instrumentation/releases/latest/download/aws-opentelemetry-agent.jar

RUN chown appuser:appgroup app.jar /opt/aws-opentelemetry-agent.jar

USER appuser

EXPOSE 8088

HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8088/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
