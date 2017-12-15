package com.nexenio.bleindoorpositioning.location.multilateration;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationTest;

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
        double[] expectedCenter = new double[]{
                6893722.565857311, 1473817.181344815}; // SOCCER_FIELD_CENTER
        double[][] positions = new double[][]{
                {6893765.004164174, 1473724.674847966}, // SOCCER_FIELD_TOP_LEFT
                {6893789.51604695, 1473894.4370714256}, // SOCCER_FIELD_TOP_RIGHT
                {6893679.213163125, 1473911.580273008}, // SOCCER_FIELD_BOTTOM_RIGHT
                {6893654.335773319, 1473740.259576677}, // SOCCER_FIELD_BOTTOM_LEFT
        };
        double distance = 125.095963; // distance from soccer field center to any edge
        double[] distances = new double[]{distance, distance, distance, distance};

        LeastSquaresOptimizer.Optimum optimum = Multilateration.findOptimum(positions, distances);
        double[] actualCenter = optimum.getPoint().toArray();

        assertEquals(expectedCenter[0], actualCenter[0], 5);
        assertEquals(expectedCenter[1], actualCenter[1], 5);

        Location actualCenterLocation = Multilateration.getLocation(optimum);
        double error = actualCenterLocation.getDistanceTo(LocationTest.SOCCER_FIELD_CENTER);
        System.out.println("Found center location: " + actualCenterLocation.generateGoogleMapsUri());
        System.out.println("Error to expected location: " + String.format("%.2f", error) + "m");
        assertEquals(0, error, 5);
    }

}