package com.max.metrics.metric.reporting;

import com.codahale.metrics.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * success rate
 * total business
 * total failure
 * timer(qps)
 * throughput, volume bytes
 */
public class LogbackReporterExample {
    private static final MetricRegistry registry = new MetricRegistry();

    private static final Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(registry)
            .outputTo(LoggerFactory.getLogger("com.max.metrics.metric.reporting.LogbackReporterExample"))
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    private static final Counter totalBusiness = new Counter();
    private static final Counter successBusiness = new Counter();
    private static final Counter failBusiness = new Counter();
    private static final Timer timer = new Timer();
    private static final Histogram volumeHistogram = new Histogram(new ExponentiallyDecayingReservoir());
    private static final RatioGauge successGauge = new RatioGauge() {
        @Override
        protected Ratio getRatio() {
            return Ratio.of(successBusiness.getCount(), totalBusiness.getCount());
        }
    };

    static {
        registry.register("cloud-disk-upload-total", totalBusiness);
        registry.register("cloud-disk-upload-success", successBusiness);
        registry.register("cloud-disk-upload-fail", failBusiness);
        registry.register("cloud-disk-upload-frequency", timer);
        registry.register("cloud-disk-upload-volume", volumeHistogram);
        registry.register("cloud-disk-upload-success-ratio", successGauge);
    }

    public static void main(String[] args) {
        slf4jReporter.start(10, TimeUnit.SECONDS);

        while (true) {
            upload(new byte[ThreadLocalRandom.current().nextInt(10_000)]);
        }
    }

    private static void upload(byte[] buffer) {
        Timer.Context context = timer.time();
        totalBusiness.inc();

        try {
            int x = 1 / ThreadLocalRandom.current().nextInt(10);

            TimeUnit.MILLISECONDS.sleep(200);
            successBusiness.inc();
            volumeHistogram.update(buffer.length);
        } catch (Exception e) {
            failBusiness.inc();
        } finally {
            context.stop();
        }
    }
}
