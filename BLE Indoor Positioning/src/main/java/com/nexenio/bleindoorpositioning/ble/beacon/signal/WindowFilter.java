package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 09.01.18.
 */

public abstract class WindowFilter implements RssiFilter {

    protected long duration;
    protected long maximumTimestamp;
    protected long minimumTimestamp;

    public WindowFilter(long duration, TimeUnit timeUnit) {
        this.minimumTimestamp = this.maximumTimestamp - timeUnit.toMillis(duration);
        this.duration = timeUnit.toMillis(duration);
    }

    /*
        Getter & Setter
     */

    public long getDuration() {
        return duration;
    }

    //TODO adjust timestamps
    public void setDuration(long duration) {
        this.duration = duration;
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
