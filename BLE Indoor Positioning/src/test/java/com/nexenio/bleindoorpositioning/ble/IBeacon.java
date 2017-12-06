package com.nexenio.bleindoorpositioning.ble;

import com.nexenio.bleindoorpositioning.location.provider.IBeaconLocationProvider;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

/**
 * Created by steppschuh on 15.11.17.
 */

public class IBeacon extends com.nexenio.bleindoorpositioning.ble.beacon.Beacon {

    @Override
    public LocationProvider createLocationProvider() {
        return new IBeaconLocationProvider(this);
    }

}
