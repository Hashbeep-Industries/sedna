package li.cil.sedna.device.rtc;

import li.cil.sedna.api.device.rtc.RealTimeCounter;

public final class SystemTimeRealTimeCounter implements RealTimeCounter {
    private static final SystemTimeRealTimeCounter INSTANCE = new SystemTimeRealTimeCounter();

    private static final int NANOSECONDS_PER_SECOND = 1_000_000_000;
    private static final int FREQUENCY = 10_000_000;

    public static RealTimeCounter get() {
        return INSTANCE;
    }

    @Override
    public long getTime() {
        final long milliseconds = System.currentTimeMillis();
        final long seconds = milliseconds / 1000;
        final long nanoseconds = System.nanoTime() % NANOSECONDS_PER_SECOND;

        return seconds * FREQUENCY + (nanoseconds / (NANOSECONDS_PER_SECOND / FREQUENCY));
    }

    @Override
    public int getFrequency() {
        return FREQUENCY;
    }
}
