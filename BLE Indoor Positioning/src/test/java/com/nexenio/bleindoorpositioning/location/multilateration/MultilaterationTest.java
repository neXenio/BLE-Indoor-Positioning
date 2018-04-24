package com.nexenio.bleindoorpositioning.location.multilateration;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationTest;
import com.nexenio.bleindoorpositioning.location.projection.SphericalMercatorProjection;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 15.12.17.
 */
public class MultilaterationTest {

    @Test
    public void findOptimum_perfectDistances_perfectOptimum() throws Exception {
        double[] expectedCenter = new double[]{0, 0};
        double[][] positions = new double[][]{{-10, 10}, {10, 10}, {10, -10}, {-10, -10}};
        double distance = 7.07106781;
        double[] distances = new double[]{distance, distance, distance, distance};

        LeastSquaresOptimizer.Optimum optimum = Multilateration.findOptimum(positions, distances);
        double[] actualCenter = optimum.getPoint().toArray();

        assertEquals(expectedCenter[0], actualCenter[0], 0);
        assertEquals(expectedCenter[1], actualCenter[1], 0);
    }

    @Test
    public void findOptimum_realisticDistances_realisticOptimum() throws Exception {
        double[] expectedCenter = new double[]{0, 0};
        double[][] positions = new double[][]{{-10, 10}, {10, 10}, {10, -10}, {-10, -10}};
        double[] distances = new double[]{7.02, 7.05, 7.09, 7.1}; // real distance to all point would be ~7.07

        LeastSquaresOptimizer.Optimum optimum = Multilateration.findOptimum(positions, distances);
        double[] actualCenter = optimum.getPoint().toArray();

        assertEquals(expectedCenter[0], actualCenter[0], 0.2);
        assertEquals(expectedCenter[1], actualCenter[1], 0.2);
    }

    @Test
    public void findOptimum_realLocations_matchingOptimum() throws Exception {
        double[] expectedCenter = new double[]{3786292.474596871, 890822.9600122868, 5037857.368752121}; // SOCCER_FIELD_CENTER

        double[][] positions = new double[][]{
                {3786285.539289399, 890763.3735945863, 5037873.011713729},  // SOCCER_FIELD_TOP_LEFT
                {3786249.7802358735, 890861.384850748, 5037882.492256797},  // SOCCER_FIELD_TOP_RIGHT
                {3786335.051375847, 890784.6461780358, 5037832.312756101},  // SOCCER_FIELD_BOTTOM_LEFT
                {3786298.9489194616, 890883.3452167381, 5037841.928865821}  // SOCCER_FIELD_BOTTOM_RIGHT

        };
        double distance = 62.384073723011014; // distance from soccer field center to any edge
        double[] distances = new double[]{distance, distance, distance, distance};

        LeastSquaresOptimizer.Optimum optimum = Multilateration.findOptimum(positions, distances);
        double[] actualCenter = optimum.getPoint().toArray();

        assertEquals(expectedCenter[0], actualCenter[0], 1);
        assertEquals(expectedCenter[1], actualCenter[1], 1);
        assertEquals(expectedCenter[2], actualCenter[2], 1);

        Location actualCenterLocation = Multilateration.getLocation(optimum);
        double error = actualCenterLocation.getDistanceTo(LocationTest.SOCCER_FIELD_CENTER);
        System.out.println("Found center location: " + actualCenterLocation.generateGoogleMapsUri());
        System.out.println("Error to expected location: " + String.format("%.2f", error) + "m");
        assertEquals(0, error, 5);
    }

    @Test
    public void location_accuracy() throws Exception {
        double[] point = new double[]{6893722.565857311, 1473817.181344815}; // SOCCER_FIELD_CENTER
        double latitude = SphericalMercatorProjection.yToLatitude(point[0]);
        double longitude = SphericalMercatorProjection.xToLongitude(point[1]);
        Location location = new Location(latitude, longitude);

        double[] adjustedPoint = new double[]{6893722.56, 1473817.18}; // SOCCER_FIELD_CENTER
        latitude = SphericalMercatorProjection.yToLatitude(adjustedPoint[0]);
        longitude = SphericalMercatorProjection.xToLongitude(adjustedPoint[1]);
        Location adjustedLocation = new Location(latitude, longitude);

        System.out.println("Location: " + location.generateGoogleMapsUri());
        System.out.println("Adjusted location: " + adjustedLocation.generateGoogleMapsUri());

        double error = adjustedLocation.getDistanceTo(location);
        System.out.println("Error to expected location: " + String.format("%.2f", error) + "m");
    }

}