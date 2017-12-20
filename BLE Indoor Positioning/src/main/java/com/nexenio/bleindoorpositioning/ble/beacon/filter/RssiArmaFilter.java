package com.nexenio.bleindoorpositioning.ble.beacon.filter;

/**
 * Created by leon on 20.12.17.
 *
 * This filter calculates its rssi on base of an auto regressive moving average (ARMA) It needs only
 * the current value to do this; the general formula is  n(t) = n(t-1) - c * (n(t-1) - n(t)) where c
 * is a coefficient, that denotes the smoothness - the lower the value, the smoother the average
 * Note: a smoother average needs longer to "settle down"
 * Note: For signals, that change rather frequently (say, 1Hz or faster) and tend to vary more a
 * recommended value would be 0,1 (that means the actual value is changed by 10% of the difference
 * between the actual measurement and the actual average) For signals at lower rates (10Hz)
 * a value of 0.25 to 0.5 would be appropriate.
 */

public class RssiArmaFilter {

    // How likely is it that the RSSI value changes?
    // Note: the more unlikely, the higher can that value be also, the lower the (expected) sending frequency,
    // the higher should that value be
    private static double DEFAULT_ARMA_FACTOR = 0.95;

    private double rssiPrediction;
    private double armaFactor;
    private boolean isInitialized = false;

    public RssiArmaFilter() {
        this.armaFactor = DEFAULT_ARMA_FACTOR;
    }

    public void addMeasurement(int rssi, int packets) {
        //use first measurement as initialization
        if (!isInitialized) {
            rssiPrediction = rssi;
            isInitialized = true;
        }
        if (packets < 10) {
            armaFactor = 0.95;
        }
        else if (packets < 20) {
            armaFactor = 0.5;
        }
        else if (packets > 20) {
            armaFactor = 0.1;
        }
        rssiPrediction = (rssiPrediction - armaFactor * (rssiPrediction - rssi));
        System.out.println("factor: " + armaFactor);
    }

    public double getFilteredRssi() {
        return rssiPrediction;
    }


}
