/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.test;

import org.apache.zookeeper.StatsTrack;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class StatsTrackTest {

    public static class OldStatsTrack {
        private int count;
        private long bytes;
        private String countStr = "count";
        private String byteStr = "bytes";

        /**
         * a default constructor for
         * stats
         */
        public OldStatsTrack() {
            this(null);
        }
        /**
         * the stat string should be of the form count=int,bytes=long
         * if stats is called with null the count and bytes are initialized
         * to -1.
         * @param stats the stat string to be intialized with
         */
        public OldStatsTrack(String stats) {
            if (stats == null) {
                stats = "count=-1,bytes=-1";
            }
            String[] split = stats.split(",");
            if (split.length != 2) {
                throw new IllegalArgumentException("invalid string " + stats);
            }
            count = Integer.parseInt(split[0].split("=")[1]);
            bytes = Long.parseLong(split[1].split("=")[1]);
        }


        /**
         * get the count of nodes allowed as part of quota
         *
         * @return the count as part of this string
         */
        public int getCount() {
            return this.count;
        }

        /**
         * set the count for this stat tracker.
         *
         * @param count
         *            the count to set with
         */
        public void setCount(int count) {
            this.count = count;
        }

        /**
         * get the count of bytes allowed as part of quota
         *
         * @return the bytes as part of this string
         */
        public long getBytes() {
            return this.bytes;
        }

        /**
         * set teh bytes for this stat tracker.
         *
         * @param bytes
         *            the bytes to set with
         */
        public void setBytes(long bytes) {
            this.bytes = bytes;
        }

        @Override
        /*
         * returns the string that maps to this stat tracking.
         */
        public String toString() {
            return countStr + "=" + count + "," + byteStr + "=" + bytes;
        }
    }

    @Test
    public void testBackwardCompatibility() {
        StatsTrack quota = new StatsTrack();
        quota.setCount(4);
        quota.setCountHardLimit(4);
        quota.setBytes(9L);
        quota.setByteHardLimit(15L);
        assertEquals("count=4,bytes=9=;byteHardLimit=15;countHardLimit=4", quota.toString());

        OldStatsTrack ost = new OldStatsTrack(quota.toString());
        assertTrue(ost.getBytes() == 9L, "bytes are set");
        assertTrue(ost.getCount() == 4, "num count is set");
        assertEquals(ost.toString(), "count=4,bytes=9");
    }

    @Test
    public void testUpwardCompatibility() {
        OldStatsTrack ost = new OldStatsTrack(null);
        ost.setCount(2);
        ost.setBytes(5);
        assertEquals("count=2,bytes=5", ost.toString());

        StatsTrack st = new StatsTrack(ost.toString());
        assertEquals("count=2,bytes=5", st.toString());
        assertEquals(5, st.getBytes());
        assertEquals(2, st.getCount());
        assertEquals(-1, st.getByteHardLimit());
        assertEquals(-1, st.getCountHardLimit());
    }
}
