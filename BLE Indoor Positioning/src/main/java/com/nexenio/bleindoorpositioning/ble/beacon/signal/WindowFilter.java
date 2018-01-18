package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 09.01.18.
 */

public abstract class WindowFilter implements RssiFilter {

    public static long DEFAULT_DURATION = TimeUnit.SECONDS.toMillis(5);

    protected long duration = DEFAULT_DURATION;
    protected long maximumTimestamp;
    protected long minimumTimestamp;
    protected TimeUnit timeUnit;

    public WindowFilter() {
        this(DEFAULT_DURATION, TimeUnit.MILLISECONDS);
    }

    public WindowFilter(long duration, TimeUnit timeUnit) {
        this.minimumTimestamp = this.maximumTimestamp - timeUnit.toMillis(duration);
        this.timeUnit = timeUnit;
    }

    /*
        Getter & Setter
     */

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.minimumTimestamp = this.maximumTimestamp - timeUnit.toMillis(duration);
    }

    public long getMaximumTimestamp() {
        return maximumTimestamp;
    }

    @Override
    public void setMaximumTimestamp(long maximumTimestamp) {
        this.maximumTimestamp = maximumTimestamp;
    }

    public long getMinimumTimestamp() {
        return minimumTimestamp;
    }

    @Override
    public void setMinimumTimestamp(long minimumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
    }
}
