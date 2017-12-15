package com.nexenio.bleindoorpositioning.location.multilateration;

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
    public void getLocation() throws Exception {
        //Location location = Multilateration.getLocation(optimum);
        //System.out.println(location.generateGoogleMapsUri());
    }

    @Test
    public void getAccuracy() throws Exception {
    }

}