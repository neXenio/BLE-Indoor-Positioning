package com.nexenio.bleindoorpositioning.location.multilateration;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

/**
 * Created by steppschuh on 14.12.17.
 *
 * "The Mathematics Behind a Local Positioning System"
 * http://inside.mines.edu/~whereman/talks/TurgutOzal-11-Trilateration.pdf
 */

public class Multilateration {

    private List<Beacon> beacons;

    private Location location;
    private float accuracy;
    private long slidingWindowDuration;
    private LeastSquaresOptimizer.Optimum optimum;

    public Multilateration(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    public LeastSquaresOptimizer.Optimum findOptimum() {
        double[][] positions = new double[][]{{5.0, -6.0}, {13.0, -15.0}, {21.0, -3.0}, {12.4, -21.2}};
        double[] distances = new double[]{8.06, 13.97, 23.32, 15.31};
        return findOptimum(positions, distances);
    }

    public static LeastSquaresOptimizer.Optimum findOptimum(double[][] positions, double[] distances) {
        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
        LeastSquaresOptimizer leastSquaresOptimizer = new LevenbergMarquardtOptimizer();
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(trilaterationFunction, leastSquaresOptimizer);
        return solver.solve();
    }

    public static Location getLocation(LeastSquaresOptimizer.Optimum optimum) {
        double[] centroid = optimum.getPoint().toArray();
        // TODO: convert to geo coordinates
        return new Location();
    }

    public static float getAccuracy(LeastSquaresOptimizer.Optimum optimum) {
        RealVector standardDeviation = optimum.getSigma(0);
        float maximumDeviation = 0;
        for (double deviation : standardDeviation.toArray()) {
            maximumDeviation = (float) Math.max(maximumDeviation, deviation);
        }
        return maximumDeviation;
    }

    /*
        Getter & Setter
     */

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public Location getLocation() {
        if (location == null) {
            location = getLocation(getOptimum());
        }
        return location;
    }

    public float getAccuracy() {
        if (accuracy == 0) {
            accuracy = getAccuracy(getOptimum());
        }
        return accuracy;
    }

    public long getSlidingWindowDuration() {
        return slidingWindowDuration;
    }

    public LeastSquaresOptimizer.Optimum getOptimum() {
        if (optimum == null) {
            optimum = findOptimum();
        }
        return optimum;
    }

}
