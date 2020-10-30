package com.max.metrics.metric.counter;

import com.codahale.metrics.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RatioGaugeCounterExample {
    private static final MetricRegistry registry = new MetricRegistry();
    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    // 计数器,可以定义为AtomicLong
    private static final Counter totalMeter = new Counter();
    private static final Counter successMeter = new Counter();

    public static void main(String[] args) {
        reporter.start(10, TimeUnit.SECONDS);
        registry.gauge("success-rate", () -> new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(successMeter.getCount(), totalMeter.getCount());
            }
        });

        for (;;) {
            sleep();
            business();
        }
    }

    private static void business() {
        // total inc
        totalMeter.inc();

        try {
            int x = 10 / ThreadLocalRandom.current().nextInt(6);
            successMeter.inc();
            // success inc
        } catch (Exception e) {
            System.out.println("Error!");
        }
    }

    private static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(6));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
