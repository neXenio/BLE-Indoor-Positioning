package com.nexenio.bleindoorpositioning.location.distance;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by steppschuh on 23.11.17.
 */
public class DistanceUtilTest {

    @Test
    public void getReasonableSmallerEvenDistance() throws Exception {
        long actual = DistanceUtil.getReasonableSmallerEvenDistance(1);
        assertEquals(1, actual);

        actual = DistanceUtil.getReasonableSmallerEvenDistance(12);
        assertEquals(10, actual);

        actual = DistanceUtil.getReasonableSmallerEvenDistance(52);
        assertEquals(50, actual);

        actual = DistanceUtil.getReasonableSmallerEvenDistance(123);
        assertEquals(100, actual);
    }

    @Test
    public void getMaximumEvenIncrement() throws Exception {
        int actual = DistanceUtil.getMaximumEvenIncrement(1);
        assertEquals(1, actual);

        actual = DistanceUtil.getMaximumEvenIncrement(12);
        assertEquals(10, actual);

        actual = DistanceUtil.getMaximumEvenIncrement(52);
        assertEquals(10, actual);

        actual = DistanceUtil.getMaximumEvenIncrement(123);
        assertEquals(100, actual);
    }

    @Test
    public void getClosestEvenDistance() throws Exception {
        long actual = DistanceUtil.getClosestEvenDistance(96, 10);
        assertEquals(100, actual);

        actual = DistanceUtil.getClosestEvenDistance(94, 10);
        assertEquals(90, actual);

        actual = DistanceUtil.getClosestEvenDistance(99, 100);
        assertEquals(100, actual);

        actual = DistanceUtil.getClosestEvenDistance(49, 100);
        assertEquals(0, actual);
    }

}