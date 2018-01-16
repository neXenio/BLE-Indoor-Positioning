package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 20.12.17.
 *
 * This filter calculates its rssi on base of an auto regressive moving average (ARMA) It needs only
 * the current value to do this; the general formula is  n(t) = n(t-1) - c * (n(t-1) - n(t)) where c
 * is a coefficient, that denotes the smoothness - the lower the value, the smoother the average
 * Note: a smoother average needs longer to "settle down" Note: For signals, that change rather
 * frequently (say, 1Hz or faster) and tend to vary more a recommended value would be 0,1 (that
 * means the actual value is changed by 10% of the difference between the actual measurement and the
 * actual average) For signals at lower rates (10Hz) a value of 0.25 to 0.5 would be appropriate.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Autoregressive–moving-average_model">Autoregressive
 * Moving Average Model</a>
 * @see <a href="https://github.com/AltBeacon/android-beacon-library/blob/master/src/main/java/org/altbeacon/beacon/service/ArmaRssiFilter.java">Example
 * Implementation</a>
 */

public class ArmaFilter extends WindowFilter {

    /**
     * Arma smoothing factor - the percentage of how much of the new signal will be discarded
     **/
    private static float DEFAULT_ARMA_FACTOR = 0.95f;

    private float armaFactor = DEFAULT_ARMA_FACTOR;
    private float armaRssi;
    private boolean isInitialized = false;

    public ArmaFilter() {
    }

    @Override
    public float filter(List<? extends AdvertisingPacket> advertisingPackets) {
        float frequency = AdvertisingPacketUtil.getPacketFrequency(advertisingPackets.size(), duration, timeUnit);
        armaFactor = getArmaFactor(frequency);
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }
            addMeasurement(advertisingPacket.getRssi(), armaFactor);
        }
        return getFilteredRssi();
    }

    public void addMeasurement(int rssi, float armaFactor) {
        //use first measurement as initialization
        if (!isInitialized) {
            armaRssi = rssi;
            isInitialized = true;
        }
        armaRssi = armaRssi - (armaFactor * (armaRssi - rssi));
    }

    public float getFilteredRssi() {
        return armaRssi;
    }

    public static float getArmaFactor(float packetFrequency) {
        //TODO make more robust to different packet frequencies
        float armaFactor = 1;
        if (packetFrequency > 6) {
            armaFactor = 0.1f;
        } else if (packetFrequency > 5) {
            armaFactor = 0.25f;
        } else if (packetFrequency > 4) {
            armaFactor = 0.5f;
        } else if (packetFrequency > 3) {
            armaFactor = 0.75f;
        }
        return armaFactor;
    }

}
