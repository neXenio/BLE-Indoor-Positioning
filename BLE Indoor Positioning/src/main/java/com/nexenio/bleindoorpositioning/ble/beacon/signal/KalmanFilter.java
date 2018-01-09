package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 02.01.18.
 *
 * Kalman filtering, also known as linear quadratic estimation (LQE), is an algorithm that uses a
 * series of measurements observed over time, containing statistical noise and other inaccuracies,
 * and produces estimates of unknown variables that tend to be more accurate than those based on a
 * single measurement alone, by using Bayesian inference and estimating a joint probability
 * distribution over the variables for each timeframe.
 *
 * <a href="https://en.wikipedia.org/wiki/Kalman_filter">Kalman Filter</a> <a
 * href="https://www.wouterbulten.nl/blog/tech/kalman-filters-explained-removing-noise-from-rssi-signals/">Kalman
 * Explained</a>
 *
 * Since RSSI signals are largely influenced by signal noise, taking samples from the signal seems
 * likely to be beneficial. --> Evaluate Unscented Kalman filter
 *
 * inspired by https://github.com/fgroch/beacon-rssi-resolver/blob/master/src/main/java/tools/blocks/filter/KalmanFilter.java
 */

public class KalmanFilter implements RssiFilter {

    public static final long DURATION_DEFAULT = TimeUnit.SECONDS.toMillis(3);

    private long minimumTimestamp;
    private long maximumTimestamp;
    private double processNoise;
    private double measurementNoise;
    private double estimatedRSSI;
    private double errorCovarianceRSSI;
    private boolean isInitialized = false;

    public KalmanFilter() {
        maximumTimestamp = System.currentTimeMillis();
        minimumTimestamp = maximumTimestamp - DURATION_DEFAULT;
    }

    public KalmanFilter(long minimumTimestamp, long maximumTimestamp) {
        this.minimumTimestamp = minimumTimestamp;
        this.maximumTimestamp = maximumTimestamp;
        //For the RSSI example we use a low value for the process noise (e.g. 0.008);
        // we assume that most of the noise is caused by the measurements.
        this.processNoise = 0.008; //0.125
        // Measurement Noise is set to a value that relates to the noise in the actual measurements
        // (e.g. the variance of the RSSI signal).
        this.measurementNoise = 1;
    }

    public KalmanFilter(long duration, TimeUnit timeUnit) {
        this();
        this.minimumTimestamp = this.maximumTimestamp - timeUnit.toMillis(duration);
        this.processNoise = 0.008;
        this.measurementNoise = 1;
    }

    @Override
    public float filter(List<? extends AdvertisingPacket> advertisingPackets) {
        double priorRSSI;
        double kalmanGain;
        double priorErrorCovarianceRSSI;

        int[] rssiArray = AdvertisingPacketUtil.getRssisFromAdvertisingPackets((List<AdvertisingPacket>) advertisingPackets);
        measurementNoise = AdvertisingPacketUtil.calculateVariance(rssiArray);

        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }

            if (!isInitialized) {
                priorRSSI = advertisingPacket.getRssi();
                priorErrorCovarianceRSSI = 1;
                isInitialized = true;
            } else {
                priorRSSI = estimatedRSSI;
                priorErrorCovarianceRSSI = errorCovarianceRSSI + processNoise;
            }

            kalmanGain = priorErrorCovarianceRSSI / (priorErrorCovarianceRSSI + measurementNoise);
            estimatedRSSI = priorRSSI + (kalmanGain * (advertisingPacket.getRssi() - priorRSSI));
            errorCovarianceRSSI = (1 - kalmanGain) * priorErrorCovarianceRSSI;
        }
        return (float) estimatedRSSI;
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
