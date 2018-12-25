/**
 * Copyright 2018 Ascendcorp, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ascendcorp.springconnectionpoolmetrics.instrument;

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
    private boolean enabledRoute;

    public PoolingHttpClientConnectionMetrics(String metricName, long sleepTime, boolean enabledRoute) {
        this.metricName = metricName;
        this.sleepTime = sleepTime;
        this.enabledRoute = enabledRoute;
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

    void createMonitor(String poolName, PoolingHttpClientConnectionManager connMgr, String metricName) {
        PoolingHttpClientConnectionMonitor monitor = new PoolingHttpClientConnectionMonitor(poolName, connMgr, sleepTime, metricName, enabledRoute);
        this.monitors.add(monitor);
    }

}
