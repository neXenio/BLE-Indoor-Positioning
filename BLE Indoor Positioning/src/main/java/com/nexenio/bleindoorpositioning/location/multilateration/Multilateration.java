package com.nexenio.bleindoorpositioning.location.multilateration;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.projection.SphericalMercatorProjection;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

/**
 * Created by steppschuh on 14.12.17.
 *
 * "The Mathematics Behind a Local Positioning System" http://inside.mines.edu/~whereman/talks/TurgutOzal-11-Trilateration.pdf
 */

public class Multilateration {

    public static final int ROOT_MEAN_SQUARE_NOT_SET = -1;

    private List<Beacon> beacons;

    private Location location;
    private float deviation;
    private double rootMeanSquare = ROOT_MEAN_SQUARE_NOT_SET;
    private LeastSquaresOptimizer.Optimum optimum;

    public Multilateration(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    public static double[][] getPositions(List<Beacon> beacons) {
        double[][] positions = new double[beacons.size()][];
        Location location;
        for (int beaconIndex = 0; beaconIndex < beacons.size(); beaconIndex++) {
            location = beacons.get(beaconIndex).getLocation();
            positions[beaconIndex] = SphericalMercatorProjection.locationToEcef(location);
        }
        return positions;
    }

    public static double[] getDistances(List<Beacon> beacons) {
        double[] distances = new double[beacons.size()];
        for (int beaconIndex = 0; beaconIndex < beacons.size(); beaconIndex++) {
            distances[beaconIndex] = beacons.get(beaconIndex).getDistance();
        }
        return distances;
    }

    public LeastSquaresOptimizer.Optimum findOptimum() {
        double[][] positions = getPositions(beacons);
        double[] distances = getDistances(beacons);
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
        Location location = SphericalMercatorProjection.ecefToLocation(centroid);
        location.setAccuracy(Math.sqrt(optimum.getRMS())); // TODO: evaluate if this is meaningful
        return location;
    }

    /**
     * Returns the maximum square root of the diagonal coefficients of the covariance matrix, as
     * provided by {@link LeastSquaresOptimizer.Optimum#getSigma(double)}.
     *
     * @see <a href="https://commons.apache.org/proper/commons-math/javadocs/api-3.4.1/org/apache/commons/math3/fitting/leastsquares/LeastSquaresProblem.Evaluation.html#getSigma(double)">LeastSquaresProblem.Evaluation
     * Documentation</a>
     */
    private static float getDeviation(LeastSquaresOptimizer.Optimum optimum) {
        RealVector standardDeviation = optimum.getSigma(0);
        float maximumDeviation = 0;
        for (double deviation : standardDeviation.toArray()) {
            maximumDeviation = (float) Math.max(maximumDeviation, deviation);
        }
        return maximumDeviation;
    }

    private static double getRMS(LeastSquaresOptimizer.Optimum optimum) {
        return optimum.getRMS();
    }

    /*
        Getter & Setter
     */

    public double getRMS() {
        if (rootMeanSquare == ROOT_MEAN_SQUARE_NOT_SET) {
            rootMeanSquare = getRMS(getOptimum());
        }
        return rootMeanSquare;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public Location getLocation() {
        if (location == null) {
            location = getLocation(getOptimum());
        }
        return location;
    }

    public float getDeviation() {
        if (deviation == 0) {
            deviation = getDeviation(getOptimum());
        }
        return deviation;
    }

    public LeastSquaresOptimizer.Optimum getOptimum() {
        if (optimum == null) {
            optimum = findOptimum();
        }
        return optimum;
    }

}
