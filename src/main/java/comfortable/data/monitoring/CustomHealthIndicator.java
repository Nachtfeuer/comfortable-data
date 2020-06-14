/*
 * The MIT License
 *
 * Copyright 2019 Thomas Lehmann.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package comfortable.data.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator.
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    /**
     * Acceptable request duration (in milliseconds).
     */
    private static final double ACCEPTABLE_REQ_DURATION = 10;

    /**
     * Metrics registry required for querying concrete metrics.
     */
    @Autowired
    private transient MeterRegistry registry;

    @Override
    public Health health() {
        final var booksPerf = queryPerformance("books.get.html");
        final var booksAuthorsPerf = queryPerformance("books.authors.get.html");
        final var booksPublishersPerf = queryPerformance("books.publishers.get.html");

        final var isHealthy = List.of(
                booksPerf.isHealthy(),
                booksAuthorsPerf.isHealthy(),
                booksPublishersPerf.isHealthy()).stream().allMatch(e -> e.equals(true));

        final Health.Builder builder;
        if (isHealthy) {
            builder = Health.up();
        } else {
            builder = Health.down();
        }

        return builder
                .withDetail("books performance", booksPerf)
                .withDetail("books authors performance", booksAuthorsPerf)
                .withDetail("books publishers performance", booksPublishersPerf)
                .build();
    }

    /**
     * Querying metric performance.
     *
     * @param metric the name of the string passed to @Timed.
     * @return performance result.
     */
    private PerformanceResult queryPerformance(final String metric) {
        final var result = this.registry.find(metric).timer();
        final var duration = (result == null) ? 0.0 : result.max(TimeUnit.MILLISECONDS);
        return PerformanceResult.builder()
                .healthy(duration < ACCEPTABLE_REQ_DURATION)
                .duration(duration)
                .unit(TimeUnit.MILLISECONDS).build();
    }
}
