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

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
class ConnectionPoolMonitorConfig {

    @Value("${connection-pool-metrics.metric-name:connection-pool}")
    private String metricName;

    @Value("${connection-pool-metrics.sleep-time:30000}")
    private long sleepTime;

    @Value("${connection-pool-metrics.enabled-route:false}")
    private boolean enabledRoute;

    @Bean
    @ConditionalOnMissingBean
    public PoolingHttpClientConnectionMetrics poolingHttpClientConnectionMetrics(ApplicationContext ctx) {
        PoolingHttpClientConnectionMetrics metrics = new PoolingHttpClientConnectionMetrics(metricName, sleepTime, enabledRoute);
        Map<String, PoolingHttpClientConnectionManager> poolingHttpClientConnectionManagers = ctx.getBeansOfType(PoolingHttpClientConnectionManager.class);
        poolingHttpClientConnectionManagers.forEach((poolName, connMgr) -> {
            metrics.createMonitor(poolName, connMgr, metricName);
        });
        return metrics;
    }

}
