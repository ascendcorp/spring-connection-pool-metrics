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

    @Value("${connection-pool-metric.metric-name:connection-pool}")
    private String metricName;

    @Value("${connection-pool-metric.sleep-time:30000}")
    private long sleepTime;

    @Bean
    @ConditionalOnMissingBean
    public PoolingHttpClientConnectionMetrics poolingHttpClientConnectionMetrics(ApplicationContext ctx) {
        PoolingHttpClientConnectionMetrics metrics = new PoolingHttpClientConnectionMetrics(metricName, sleepTime);
        Map<String, PoolingHttpClientConnectionManager> poolingHttpClientConnectionManagers = ctx.getBeansOfType(PoolingHttpClientConnectionManager.class);
        poolingHttpClientConnectionManagers.forEach(metrics::createMonitor);
        return metrics;
    }

}
