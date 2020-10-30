package com.max.metrics.metric.gauge;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RatioGaugeExample {
    private static final MetricRegistry registry = new MetricRegistry();
    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    // 计数器,可以定义为AtomicLong
    private static final Meter totalMeter = new Meter();
    private static final Meter successMeter = new Meter();

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
        totalMeter.mark();

        try {
            int x = 10 / ThreadLocalRandom.current().nextInt(6);
            successMeter.mark();
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
