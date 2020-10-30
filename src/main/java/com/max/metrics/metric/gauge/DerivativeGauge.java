package com.max.metrics.metric.gauge;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reporter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DerivativeGauge {
    private static final MetricRegistry registry = new MetricRegistry();
    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    private static final LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .recordStats()
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return s.toUpperCase();
                }
            });

    public static void main(String[] args) throws InterruptedException {
        reporter.start(10, TimeUnit.SECONDS);
        //registry.gauge("cache-stats", () -> cache::stats);
        Gauge<CacheStats> cacheGauge = registry.gauge("cache-stats", () -> cache::stats);
        registry.register("missCount", new com.codahale.metrics.DerivativeGauge<CacheStats, Long>(cacheGauge) {
            @Override
            protected Long transform(CacheStats cacheStats) {
                return cacheStats.missCount();
            }
        });

        registry.register("loadExceptionCount", new com.codahale.metrics.DerivativeGauge<CacheStats, Long>(cacheGauge) {
            @Override
            protected Long transform(CacheStats cacheStats) {
                return cacheStats.loadExceptionCount();
            }
        });


        while (true) {
            business();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void business() {
        cache.getUnchecked("alex");
    }
}
