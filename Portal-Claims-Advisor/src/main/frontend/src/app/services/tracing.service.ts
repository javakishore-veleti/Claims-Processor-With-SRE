import { Injectable } from '@angular/core';
import { WebTracerProvider } from '@opentelemetry/sdk-trace-web';
import { BatchSpanProcessor } from '@opentelemetry/sdk-trace-base';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { ZoneContextManager } from '@opentelemetry/context-zone';
import { Resource } from '@opentelemetry/resources';
import { ATTR_SERVICE_NAME } from '@opentelemetry/semantic-conventions';
import { registerInstrumentations } from '@opentelemetry/instrumentation';
import { FetchInstrumentation } from '@opentelemetry/instrumentation-fetch';
import { DocumentLoadInstrumentation } from '@opentelemetry/instrumentation-document-load';
import { UserInteractionInstrumentation } from '@opentelemetry/instrumentation-user-interaction';

@Injectable({ providedIn: 'root' })
export class TracingService {
  private initialized = false;

  init(serviceName: string) {
    if (this.initialized) return;
    this.initialized = true;

    try {
      const provider = new WebTracerProvider({
        resource: new Resource({
          [ATTR_SERVICE_NAME]: serviceName,
        }),
      });

      // Only export if collector is available (dev environment)
      const collectorUrl = 'http://localhost:4318/v1/traces';
      provider.addSpanProcessor(
        new BatchSpanProcessor(
          new OTLPTraceExporter({ url: collectorUrl })
        )
      );

      provider.register({
        contextManager: new ZoneContextManager(),
      });

      registerInstrumentations({
        instrumentations: [
          new FetchInstrumentation({
            propagateTraceHeaderCorsUrls: [/localhost/],
          }),
          new DocumentLoadInstrumentation(),
          new UserInteractionInstrumentation(),
        ],
      });

      console.log(`OpenTelemetry initialized for ${serviceName}`);
    } catch (e) {
      console.warn('OpenTelemetry initialization failed (collector may not be running):', e);
    }
  }
}
