package com.max.metrics.metric.meter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Meter: A meter measures the rate at which a set of events occur
 */
public class MeterExample {
    private final static MetricRegistry registry = new MetricRegistry();
    private final static Meter requestMeter = registry.meter("tqs");
    private final static Meter sizeMeter = registry.meter("volume");

    public static void main(String[] args) {
        // Reporting: ConsoleReporting, CsvReporting, SLF4JReporting, JMXReporting, Others
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.MINUTES)
                .convertDurationsTo(TimeUnit.MINUTES)
                .build();
        reporter.start(10, TimeUnit.SECONDS);

        for (;;) {
            handleRequest(new byte[ThreadLocalRandom.current().nextInt(1000)]);
            randomSleep();
        }
    }

    private static void handleRequest(byte[] request) {
        requestMeter.mark();
        sizeMeter.mark(request.length);
        randomSleep();
    }

    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
