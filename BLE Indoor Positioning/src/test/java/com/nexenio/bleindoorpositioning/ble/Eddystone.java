package com.nexenio.bleindoorpositioning.ble;

import com.nexenio.bleindoorpositioning.location.provider.EddystoneLocationProvider;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

/**
 * Created by steppschuh on 15.11.17.
 */

public class Eddystone extends Beacon {

    @Override
    public LocationProvider createLocationProvider() {
        return new EddystoneLocationProvider(this);
    }

}
