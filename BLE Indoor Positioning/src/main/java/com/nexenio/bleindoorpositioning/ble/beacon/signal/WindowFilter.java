package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;

import java.util.List;
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
        this(DEFAULT_DURATION, TimeUnit.MILLISECONDS, System.currentTimeMillis());
    }

    public WindowFilter(long duration, TimeUnit timeUnit) {
        this(duration, timeUnit, System.currentTimeMillis());
    }

    public WindowFilter(long maximumTimestamp) {
        this(DEFAULT_DURATION, TimeUnit.MILLISECONDS, maximumTimestamp);
    }

    public WindowFilter(long duration, TimeUnit timeUnit, long maximumTimestamp) {
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.maximumTimestamp = maximumTimestamp;
        this.minimumTimestamp = maximumTimestamp - timeUnit.toMillis(duration);
    }

    public void updateDuration() {
        duration = maximumTimestamp - minimumTimestamp;
    }

    public List<AdvertisingPacket> getRecentAdvertisingPackets(Beacon beacon) {
        return beacon.getAdvertisingPacketsBetween(minimumTimestamp, maximumTimestamp + 1);
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

    public long getMaximumTimestamp() {
        return maximumTimestamp;
    }

    public void setMaximumTimestamp(long maximumTimestamp) {
        this.maximumTimestamp = maximumTimestamp;
        updateDuration();
    }

    public long getMinimumTimestamp() {
        return minimumTimestamp;
    }

    public void setMinimumTimestamp(long minimumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
        updateDuration();
    }

}
