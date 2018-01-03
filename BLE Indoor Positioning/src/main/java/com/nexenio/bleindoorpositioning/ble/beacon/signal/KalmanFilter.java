package com.nexenio.bleindoorpositioning.ble.beacon.signal;

/**
 * Created by leon on 02.01.18.
 *
 * https://github.com/fgroch/beacon-rssi-resolver/blob/master/src/main/java/tools/blocks/filter/KalmanFilter.java
 */

public class KalmanFilter {

    private double processNoise;
    private double measurementNoise;
    private double estimatedRSSI;
    private double errorCovarianceRSSI;
    private boolean isInitialized = false;

    public KalmanFilter() {
        this.processNoise = 0.125;
        this.measurementNoise = 0.8;
    }

    public KalmanFilter(double processNoise, double measurementNoise) {
        this.processNoise = processNoise;
        this.measurementNoise = measurementNoise;
    }

    public double applyFilter(float rssi) {
        double priorRSSI;
        double kalmanGain;
        double priorErrorCovarianceRSSI;
        if (!isInitialized) {
            priorRSSI = rssi;
            priorErrorCovarianceRSSI = 1;
            isInitialized = true;
        } else {
            priorRSSI = estimatedRSSI;
            priorErrorCovarianceRSSI = errorCovarianceRSSI + processNoise;
        }

        kalmanGain = priorErrorCovarianceRSSI / (priorErrorCovarianceRSSI + measurementNoise);
        estimatedRSSI = priorRSSI + (kalmanGain * (rssi - priorRSSI));
        errorCovarianceRSSI = (1 - kalmanGain) * priorErrorCovarianceRSSI;

        return estimatedRSSI;
    }
}
