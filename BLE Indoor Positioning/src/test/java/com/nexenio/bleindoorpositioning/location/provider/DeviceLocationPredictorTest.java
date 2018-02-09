package com.nexenio.bleindoorpositioning.location.provider;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 31.01.18.
 */
public class DeviceLocationPredictorTest {

    @Test
    public void calculateBearing_validLocations_accurateBearing() throws Exception {
        assertEquals((int) DeviceLocationPredictor.calculateBearing(43.682213,-70.450696, 43.682194, -70.450769),250);
        assertEquals((int) DeviceLocationPredictor.calculateBearing(8.46696, -17.03663, 65.35996, -17.03663),0);
        assertEquals((int) DeviceLocationPredictor.calculateBearing(8.46696, -17.03663, 64.1609177685091, -30.044444500000054), 353);
        assertEquals((int) DeviceLocationPredictor.calculateBearing(52.666402, 9.0888582, 52.806694, 8.9992430), 338);
    }

}