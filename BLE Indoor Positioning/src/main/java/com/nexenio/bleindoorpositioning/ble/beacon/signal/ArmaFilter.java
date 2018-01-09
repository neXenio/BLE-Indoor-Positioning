package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 20.12.17.
 *
 * taken from https://github.com/AltBeacon/android-beacon-library/blob/master/src/main/java/org/altbeacon/beacon/service/ArmaRssiFilter.java
 *
 * This filter calculates its rssi on base of an auto regressive moving average (ARMA) It needs only
 * the current value to do this; the general formula is  n(t) = n(t-1) - c * (n(t-1) - n(t)) where c
 * is a coefficient, that denotes the smoothness - the lower the value, the smoother the average
 * Note: a smoother average needs longer to "settle down" Note: For signals, that change rather
 * frequently (say, 1Hz or faster) and tend to vary more a recommended value would be 0,1 (that
 * means the actual value is changed by 10% of the difference between the actual measurement and the
 * actual average) For signals at lower rates (10Hz) a value of 0.25 to 0.5 would be appropriate.
 *
 * <a href="https://en.wikipedia.org/wiki/Autoregressive–moving-average_model">Autoregressive Moving
 * Average Model</a>
 */

public class ArmaFilter implements RssiFilter {

    // How likely is it that the RSSI value changes?
    // Note: the more unlikely, the higher can that value be also, the lower the (expected) sending frequency,
    // the higher should that value be
    private static float DEFAULT_ARMA_FACTOR = 0.95f;
    public static final long DURATION_DEFAULT = TimeUnit.SECONDS.toMillis(3);

    private long minimumTimestamp;
    private long maximumTimestamp;
    private static float armaFactor;
    private float armaRssi;
    private boolean isInitialized = false;
    private long duration;

    public ArmaFilter() {
        maximumTimestamp = System.currentTimeMillis();
        minimumTimestamp = maximumTimestamp - DURATION_DEFAULT;
    }

    public ArmaFilter(long minimumTimestamp, long maximumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
        this.maximumTimestamp = maximumTimestamp;
        this.armaFactor = DEFAULT_ARMA_FACTOR;
    }

    public ArmaFilter(long duration, TimeUnit timeUnit) {
        this();
        this.minimumTimestamp = this.maximumTimestamp - timeUnit.toMillis(duration);
        this.duration = duration;
    }

    @Override
    public float filter(List<? extends AdvertisingPacket> advertisingPackets) {
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }
            addMeasurement(advertisingPacket.getRssi(),getArmaFactor(getPacketFrequency(advertisingPackets.size(),duration)));
            System.out.println("duration: " + duration);
        }
        return getFilteredRssi();
    }

    public void addMeasurement(int rssi, float packetFrequency) {
        //use first measurement as initialization
        if (!isInitialized) {
            armaRssi = rssi;
            isInitialized = true;
        }
        armaRssi = armaRssi - (getArmaFactor(packetFrequency) * (armaRssi - rssi));
    }

    public float getFilteredRssi() {
        return armaRssi;
    }

    public static float getArmaFactor(float packetFrequency) {
        if (packetFrequency > 4) {
            armaFactor = 0.1f;
        } else if (packetFrequency > 3) {
            armaFactor = 0.25f;
        } else if (packetFrequency > 2) {
            armaFactor = 0.5f;
        } else if (packetFrequency > 1) {
            armaFactor = 0.75f;
        }
        return armaFactor;
    }

    public static float getPacketFrequency(float packets, float time) {
        //TODO make output more robust to different time inputs
        return packets / (time / 1000);
    }

    /*
        Getter & Setter
     */

    public long getMinimumTimestamp() {
        return minimumTimestamp;
    }

    public void setMinimumTimestamp(long minimumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
    }

    public long getMaximumTimestamp() {
        return maximumTimestamp;
    }

    public void setMaximumTimestamp(long maximumTimestamp) {
        this.maximumTimestamp = maximumTimestamp;
    }

}
