package com.truemoney.api.springconnectionpoolmetrics.instrument;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.lang.NonNull;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.List;

class PoolingHttpClientConnectionMetrics implements MeterBinder {

    private final List<PoolingHttpClientConnectionMonitor> monitors;
    private String metricName;
    private long sleepTime;

    public PoolingHttpClientConnectionMetrics(String metricName, long sleepTime) {
        this.metricName = metricName;
        this.sleepTime = sleepTime;
        this.monitors = new ArrayList<>();
    }

    @Override
    public void bindTo(@NonNull MeterRegistry meterRegistry) {

        monitors.forEach(monitor -> {
            Gauge.builder(metricName, monitor, PoolingHttpClientConnectionMonitor::getAvailable)
                    .tags("id", "available", "name", monitor.getPoolName())
                    .description("available connections")
                    .register(meterRegistry);

            Gauge.builder(metricName, monitor, PoolingHttpClientConnectionMonitor::getLeased)
                    .tags("id", "leased", "name", monitor.getPoolName())
                    .description("leased connections")
                    .register(meterRegistry);

            Gauge.builder(metricName, monitor, PoolingHttpClientConnectionMonitor::getPending)
                    .tags("id", "pending", "name", monitor.getPoolName())
                    .description("pending connections")
                    .register(meterRegistry);

            Gauge.builder(metricName, monitor, PoolingHttpClientConnectionMonitor::getMax)
                    .tags("id", "max", "name", monitor.getPoolName())
                    .description("max connections")
                    .register(meterRegistry);
        });

    }

    void createMonitor(String metricName, PoolingHttpClientConnectionManager connMgr) {
        PoolingHttpClientConnectionMonitor monitor = new PoolingHttpClientConnectionMonitor(metricName, connMgr, sleepTime);
        this.monitors.add(monitor);
    }

}
