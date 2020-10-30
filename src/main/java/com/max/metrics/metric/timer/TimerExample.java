package com.max.metrics.metric.timer;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TimerExample {
    private static final MetricRegistry registry = new MetricRegistry();
    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    private static final Timer timer = registry.timer("request", Timer::new);

    public static void main(String[] args) {
        reporter.start(10, TimeUnit.SECONDS);

        while (true) {
            business();
        }
    }

    private static void business() {
        Timer.Context context = timer.time();

        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            long stop = context.stop();
            System.out.println("=============" + stop);
        }
    }
}
