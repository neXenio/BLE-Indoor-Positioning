package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacketUtil;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;

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
 * likely to be beneficial. Evaluate Unscented Kalman filter
 *
 * @see <a href="https://en.wikipedia.org/wiki/Kalman_filter">Kalman Filter</a>
 * @see <a href="https://www.wouterbulten.nl/blog/tech/kalman-filters-explained-removing-noise-from-rssi-signals/">Kalman
 * Explained</a>
 * @see <a href="https://github.com/fgroch/beacon-rssi-resolver/blob/master/src/main/java/tools/blocks/filter/KalmanFilter.java">Example
 * Implementation</a>
 */

public class KalmanFilter extends WindowFilter {

    /**
     * We use a low value for the process noise (i.e. 0.008). We assume that most of the noise is
     * caused by the measurements.
     **/
    private static float PROCESS_NOISE_DEFAULT = 0.008f;

    private float processNoise = PROCESS_NOISE_DEFAULT;

    public KalmanFilter() {
    }

    public KalmanFilter(long duration, TimeUnit timeUnit) {
        super(duration, timeUnit);
    }

    public KalmanFilter(long maximumTimestamp) {
        super(maximumTimestamp);
    }

    public KalmanFilter(long duration, TimeUnit timeUnit, long maximumTimestamp) {
        super(duration, timeUnit, maximumTimestamp);
    }

    @Override
    public float filter(Beacon beacon) {
        List<AdvertisingPacket> advertisingPackets = getRecentAdvertisingPackets(beacon);
        int[] rssiArray = AdvertisingPacketUtil.getRssisFromAdvertisingPackets(advertisingPackets);
        // Measurement noise is set to a value that relates to the noise in the actual measurements
        // (i.e. the variance of the RSSI signal).
        float measurementNoise = AdvertisingPacketUtil.calculateVariance(rssiArray);
        // used for initialization of kalman filter
        float meanRssi = AdvertisingPacketUtil.calculateMean(rssiArray);
        return calculateKalmanRssi(advertisingPackets, processNoise, measurementNoise, meanRssi);
    }

    private static float calculateKalmanRssi(List<AdvertisingPacket> advertisingPackets,
                                             float processNoise, float measurementNoise, float meanRssi) {
        float errorCovarianceRssi;
        float lastErrorCovarianceRssi = 1;
        float estimatedRssi = meanRssi;
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            float kalmanGain = lastErrorCovarianceRssi / (lastErrorCovarianceRssi + measurementNoise);
            estimatedRssi = estimatedRssi + (kalmanGain * (advertisingPacket.getRssi() - estimatedRssi));
            errorCovarianceRssi = (1 - kalmanGain) * lastErrorCovarianceRssi;
            lastErrorCovarianceRssi = errorCovarianceRssi + processNoise;
        }
        return estimatedRssi;
    }

    /*
        Getter & Setter
     */

    public float getProcessNoise() {
        return processNoise;
    }

    public void setProcessNoise(float processNoise) {
        this.processNoise = processNoise;
    }

}
