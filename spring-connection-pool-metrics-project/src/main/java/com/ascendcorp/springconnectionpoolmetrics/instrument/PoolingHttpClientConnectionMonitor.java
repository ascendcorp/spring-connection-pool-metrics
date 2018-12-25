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

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;

/**
 * A thread that periodically get pool statistics from a monitored PoolingHttpClientConnectionManager object.
 * The sleep time between each stats check should not be too frequently due to the fact that the implementation of the
 * connection pool used by PoolingHttpClientConnectionManager is locked during the call to its getTotalStats() method.
 */
public class PoolingHttpClientConnectionMonitor extends Thread {

    private final PoolingHttpClientConnectionManager connMgr;

    private volatile boolean shutdown;
    private String poolName;
    private PoolStats poolStats;
    private long sleepTime;
    private String metricPerRouteName;
    private boolean enabledRoute;


    public PoolingHttpClientConnectionMonitor(String poolName, PoolingHttpClientConnectionManager connMgr, long sleepTime, String metricName, boolean enabledRoute) {
        this.poolName = poolName;
        this.connMgr = connMgr;
        this.poolStats = connMgr.getTotalStats();
        this.sleepTime = sleepTime;
        this.metricPerRouteName = metricName + "_per_route";
        this.enabledRoute = enabledRoute;

        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(sleepTime);
                    this.poolStats = this.connMgr.getTotalStats();
                    if (enabledRoute)
                        this.connMgr.getRoutes().forEach(route -> updateConnectionPoolPerRoute(route, this.connMgr.getStats(route)));
                }
            }
        } catch (InterruptedException ex) {
            shutdown();
        }
    }

    private void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

    private void updateConnectionPoolPerRoute(HttpRoute route, PoolStats poolStats) {
        Metrics.gauge(metricPerRouteName, Tags.of("id", "available", "name", this.poolName, "route", route.getTargetHost().getHostName()), poolStats.getAvailable());
        Metrics.gauge(metricPerRouteName, Tags.of("id", "leased", "name", this.poolName, "route", route.getTargetHost().getHostName()), poolStats.getLeased());
        Metrics.gauge(metricPerRouteName, Tags.of("id", "pending", "name", this.poolName, "route", route.getTargetHost().getHostName()), poolStats.getPending());
        Metrics.gauge(metricPerRouteName, Tags.of("id", "max", "name", this.poolName, "route", route.getTargetHost().getHostName()), poolStats.getMax());
    }

    String getPoolName() {
        return poolName;
    }

    int getAvailable() {
        return this.poolStats.getAvailable();
    }

    int getLeased() {
        return this.poolStats.getLeased();
    }

    int getPending() {
        return this.poolStats.getPending();
    }

    int getMax() {
        return this.poolStats.getMax();
    }

}
