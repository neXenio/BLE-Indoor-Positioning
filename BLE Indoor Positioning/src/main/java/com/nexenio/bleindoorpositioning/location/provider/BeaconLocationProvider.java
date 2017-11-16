package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 15.11.17.
 */

public abstract class BeaconLocationProvider<T extends Beacon> implements LocationProvider {

    protected T beacon;
    protected Location location;

    public BeaconLocationProvider(T beacon) {
        this.beacon = beacon;
    }

    public abstract Location updateLocation();

    public boolean shouldUpdateLocation() {
        return location == null;
    }

    @Override
    public Location getLocation() {
        if (shouldUpdateLocation()) {
            location = updateLocation();
        }
        return location;
    }

}
