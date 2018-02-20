package com.nexenio.bleindoorpositioning.location.angle;

import org.junit.Test;

import static com.nexenio.bleindoorpositioning.location.angle.AngleUtil.calculateMeanAngle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by leon on 14.02.18.
 */
public class AngleUtilTest {

    @Test
    public void calculateAngleMean_validAngles_correctAngle() throws Exception {
        double angles[] = {10, 350, 0, 20, 340};
        double expectedAngle = 0;
        double actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0.00001);

        angles = new double[]{10};
        expectedAngle = 10;
        actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0);

        angles = new double[]{0, 0, 90};
        expectedAngle = 26.565;
        actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0.0001);
    }

    @Test
    public void calculateAngleMean_invalidAngles_correctAngle() throws Exception {
        double angles[] = null;
        double expectedAngle = 0;
        double actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0);

        angles = new double[]{};
        expectedAngle = 0;
        actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0);
    }

    // negative values not supported currently
    @Test
    public void calculateAngleMean_negativeAngles_unequalAngles() throws Exception {
        double angles[] = new double[]{-90};
        double negativeAngle = calculateMeanAngle(angles);
        angles = new double[]{270};
        double positiveAngle = calculateMeanAngle(angles);
        assertNotEquals(negativeAngle, positiveAngle);
    }

}