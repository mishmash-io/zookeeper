/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.metrics;

import static java.lang.Math.floor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * Generic long counter that keep track of min/max/avg/percentiles.
 * The counter is thread-safe
 */
public class AvgMinMaxPercentileCounter extends Metric implements Summary {

    private final String name;
    private final AvgMinMaxCounter counter;
    private final ResettableUniformReservoir reservoir;

    static class ResettableUniformReservoir {

        private static final int DEFAULT_SIZE = 4096;
        private static final int BITS_PER_LONG = 63;

        private final AtomicLong count = new AtomicLong();
        private volatile AtomicLongArray values = new AtomicLongArray(DEFAULT_SIZE);

        public int size() {
            final long c = count.get();
            if (c > values.length()) {
                return values.length();
            }
            return (int) c;
        }

        public void update(long value) {
            final long c = count.incrementAndGet();
            if (c <= values.length()) {
                values.set((int) c - 1, value);
            } else {
                final long r = nextLong(c);
                if (r < values.length()) {
                    values.set((int) r, value);
                }
            }
        }

        private static long nextLong(long n) {
            long bits, val;
            do {
                bits = ThreadLocalRandom.current().nextLong() & (~(1L << BITS_PER_LONG));
                val = bits % n;
            } while (bits - val + (n - 1) < 0L);
            return val;
        }

        public Snapshot getSnapshot() {
            final int s = size();
            final List<Long> copy = new ArrayList<>(s);
            for (int i = 0; i < s; i++) {
                copy.add(values.get(i));
            }
            return new Snapshot(copy);
        }

        public void reset() {
            count.set(0);
            values = new AtomicLongArray(DEFAULT_SIZE);
        }

    }

    static class Snapshot {
        private long[] values;

        public Snapshot(Collection<Long> col) {
            values = col.stream().mapToLong(l -> l.longValue()).sorted().toArray();
        }

        public double getValue(double quantile) {
            if (quantile < 0.0 || quantile > 1.0 || Double.isNaN(quantile)) {
                throw new IllegalArgumentException(quantile + " is not in [0..1]");
            }

            if (values.length == 0) {
                return 0.0;
            }

            double pos = quantile * (values.length + 1);
            int index = (int) pos;

            if (index < 1) {
                return values[0];
            }

            if (index >= values.length) {
                return values[values.length - 1];
            }

            double lower = values[index - 1];
            double upper = values[index];

            return lower + (pos - floor(pos)) * (upper - lower);
        }

        public double getMedian() {
            return getValue(0.5);
        }

        public double get75thPercentile() {
            return getValue(0.75);
        }

        public double get95thPercentile() {
            return getValue(0.95);
        }

        public double get98thPercentile() {
            return getValue(0.98);
        }

        public double get99thPercentile() {
            return getValue(0.99);
        }

        public double get999thPercentile() {
            return getValue(0.999);
        }
    }

    public AvgMinMaxPercentileCounter(String name) {

        this.name = name;
        this.counter = new AvgMinMaxCounter(this.name);
        reservoir = new ResettableUniformReservoir();
    }

    public void addDataPoint(long value) {
        counter.add(value);
        reservoir.update(value);
    }

    public void resetMax() {
        // To match existing behavior in upstream
        counter.resetMax();
    }

    public void reset() {
        counter.reset();
        reservoir.reset();
    }

    public void add(long value) {
        addDataPoint(value);
    }

    public Map<String, Object> values() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.putAll(counter.values());
        
        Snapshot s = reservoir.getSnapshot();
        m.put("p50_" + name, Math.round(s.getMedian()));
        m.put("p95_" + name, Math.round(s.get95thPercentile()));
        m.put("p99_" + name, Math.round(s.get99thPercentile()));
        m.put("p999_" + name, Math.round(s.get999thPercentile()));
        return m;
    }

}
