package com.max.metrics.metric.histogram;

import com.codahale.metrics.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ExponentiallyDecayingReservoirsHistogramExample {

    private static final MetricRegistry registry = new MetricRegistry();
    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    private static final Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());

    public static void main(String[] args) {
        reporter.start(10, TimeUnit.SECONDS);
        registry.register("ExponentiallyDecayingReservoir", histogram);

        while (true) {
            doSearch();
            randomSleep();
        }
    }

    private static void doSearch() {
        histogram.update(ThreadLocalRandom.current().nextInt(10));
    }

    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
