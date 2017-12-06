package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.location.provider.IBeaconLocationProvider;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

/**
 * Created by steppschuh on 15.11.17.
 */

public class IBeacon extends Beacon {

    public static final int CALIBRATION_DISTANCE_DEFAULT = 0;

    public IBeacon() {
        this.calibratedDistance = CALIBRATION_DISTANCE_DEFAULT;
    }

    @Override
    public LocationProvider createLocationProvider() {
        return new IBeaconLocationProvider(this);
    }

}
