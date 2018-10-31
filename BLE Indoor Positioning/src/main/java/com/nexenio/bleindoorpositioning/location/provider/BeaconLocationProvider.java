package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 15.11.17.
 */

public abstract class BeaconLocationProvider<B extends Beacon> implements LocationProvider {

    protected B beacon;
    protected Location location;

    public BeaconLocationProvider(B beacon) {
        this.beacon = beacon;
    }

    protected abstract void updateLocation();

    protected boolean shouldUpdateLocation() {
        return location == null;
    }

    protected abstract boolean canUpdateLocation();

    @Override
    public Location getLocation() {
        if (shouldUpdateLocation() && canUpdateLocation()) {
            updateLocation();
        }
        return location;
    }

    public boolean hasLocation() {
        Location location = getLocation();
        return location != null && location.hasLatitudeAndLongitude();
    }

}
