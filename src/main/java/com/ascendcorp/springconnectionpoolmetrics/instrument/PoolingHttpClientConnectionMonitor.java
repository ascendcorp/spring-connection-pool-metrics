package com.ascendcorp.springconnectionpoolmetrics.instrument;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;

/**
 * A thread that periodically get pool statistics from a monitored PoolingHttpClientConnectionManager object.
 * The sleep time between each stats check should not be too frequently due to the fact that the implementation of the
 * connection pool used by PoolingHttpClientConnectionManager is locked during the call to its getTotalStats() method.
 */
public class PoolingHttpClientConnectionMonitor extends Thread {

    private final PoolingHttpClientConnectionManager connMgr;
    private long sleepTime;
    private volatile boolean shutdown;
    private String poolName;
    private PoolStats poolStats;

    public PoolingHttpClientConnectionMonitor(String poolName, PoolingHttpClientConnectionManager connMgr, long sleepTime) {
        this.poolName = poolName;
        this.connMgr = connMgr;
        this.poolStats = connMgr.getTotalStats();
        this.sleepTime = sleepTime;

        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(sleepTime);
                    this.poolStats = connMgr.getTotalStats();
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
