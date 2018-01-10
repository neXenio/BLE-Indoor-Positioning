package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 09.01.18.
 */

public abstract class WindowFilter implements RssiFilter {

    protected static final long DURATION_DEFAULT = TimeUnit.SECONDS.toMillis(3);

    protected long duration = DURATION_DEFAULT;
    protected long maximumTimestamp = System.currentTimeMillis();
    protected long minimumTimestamp = maximumTimestamp - DURATION_DEFAULT;

    public WindowFilter(long duration, TimeUnit timeUnit){
        this.minimumTimestamp = this.maximumTimestamp - timeUnit.toMillis(duration);
        this.duration = duration;
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
