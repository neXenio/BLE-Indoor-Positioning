package com.nexenio.bleindoorpositioning.location.angle;

import org.junit.Test;

import static com.nexenio.bleindoorpositioning.location.angle.AngleUtil.calculateMeanAngle;
import static org.junit.Assert.assertEquals;

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

        angles = null;
        expectedAngle = 0;
        actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0);

        angles = new double[]{};
        expectedAngle = 0;
        actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0);

        angles = new double[]{10};
        expectedAngle = 10;
        actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0);

        angles = new double[]{0, 360};
        expectedAngle = 0;
        actualAngle = calculateMeanAngle(angles);
        assertEquals(expectedAngle, actualAngle, 0.00001);
    }
}