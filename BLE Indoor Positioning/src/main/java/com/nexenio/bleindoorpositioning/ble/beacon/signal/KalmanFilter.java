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
 *
 * Since RSSI signals are largely influenced by signal noise, taking samples from the signal seems
 * likely to be beneficial. --> Evaluate Unscented Kalman filter
 *
 * @see <a href="https://en.wikipedia.org/wiki/Kalman_filter">Kalman Filter</a>
 * @see <a href="https://www.wouterbulten.nl/blog/tech/kalman-filters-explained-removing-noise-from-rssi-signals/">Kalman
 * Explained</a>
 * @see <a href="https://github.com/fgroch/beacon-rssi-resolver/blob/master/src/main/java/tools/blocks/filter/KalmanFilter.java">Example
 * Implementation</a>
 */

public class KalmanFilter extends WindowFilter {

    /**
     * We use a low value for the process noise (i.e. 0.008).
     * We assume that most of the noise is caused by the measurements.
     **/
    private static float DEFAULT_PROCESS_NOISE = 0.008f;
    /**
     * Measurement noise is set to a value that relates to the noise in the actual measurements
     * (i.e. the variance of the RSSI signal).
     */
    private static float DEFAULT_MEASUREMENT_NOISE = 10;

    private float processNoise = DEFAULT_PROCESS_NOISE;
    private float measurementNoise = DEFAULT_MEASUREMENT_NOISE;
    private float estimatedRssi;
    private float errorCovarianceRssi;
    private boolean isInitialized = false;

    public KalmanFilter() {
    }

    public KalmanFilter(long duration, TimeUnit timeUnit) {
        super(duration,timeUnit);
    }

    public KalmanFilter(long duration, TimeUnit timeUnit, float processNoise, float measurementNoise){
        super(duration,timeUnit);
        this.processNoise = processNoise;
        this.measurementNoise = measurementNoise;
    }

    @Override
    public float filter(List<? extends AdvertisingPacket> advertisingPackets) {
        float priorRssi;
        float kalmanGain;
        float priorErrorCovarianceRssi;

        int[] rssiArray = AdvertisingPacketUtil.getRssisFromAdvertisingPackets(advertisingPackets);
        measurementNoise = AdvertisingPacketUtil.calculateVariance(rssiArray);

        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket.getTimestamp() < minimumTimestamp) {
                continue;
            }

            if (!isInitialized) {
                priorRssi = advertisingPacket.getRssi();
                priorErrorCovarianceRssi = 1;
                isInitialized = true;
            } else {
                priorRssi = estimatedRssi;
                priorErrorCovarianceRssi = errorCovarianceRssi + processNoise;
            }

            kalmanGain = priorErrorCovarianceRssi / (priorErrorCovarianceRssi + measurementNoise);
            estimatedRssi = priorRssi + (kalmanGain * (advertisingPacket.getRssi() - priorRssi));
            errorCovarianceRssi = (1 - kalmanGain) * priorErrorCovarianceRssi;
        }
        return estimatedRssi;
    }

}
