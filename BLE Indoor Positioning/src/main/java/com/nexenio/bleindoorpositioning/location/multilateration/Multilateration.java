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
 * "The Mathematics Behind a Local Positioning System"
 * http://inside.mines.edu/~whereman/talks/TurgutOzal-11-Trilateration.pdf
 */

public class Multilateration {

    private List<Beacon> beacons;

    private Location location;
    private float deviation;
    private LeastSquaresOptimizer.Optimum optimum;

    public Multilateration(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    public static double[][] getPositions(List<Beacon> beacons) {
        double[][] positions = new double[beacons.size()][];
        Location location;
        for (int beaconIndex = 0; beaconIndex < beacons.size(); beaconIndex++) {
            location = beacons.get(beaconIndex).getLocation();
            positions[beaconIndex] = new double[]{
                    SphericalMercatorProjection.latitudeToY(location.getLatitude()),
                    SphericalMercatorProjection.longitudeToX(location.getLongitude())
                    // TODO: add altitude
            };
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
        double latitude = SphericalMercatorProjection.yToLatitude(centroid[0]);
        double longitude = SphericalMercatorProjection.xToLongitude(centroid[1]);
        // TODO: add altitude
        return new Location(latitude, longitude);
    }

    public static float getDeviation(LeastSquaresOptimizer.Optimum optimum) {
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
