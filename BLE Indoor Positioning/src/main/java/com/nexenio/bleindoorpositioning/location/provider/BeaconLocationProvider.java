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

    protected abstract void updateLocation();

    protected boolean shouldUpdateLocation() {
        return location == null;
    }

    protected boolean canUpdateLocation() {
        return beacon.hasAnyAdvertisingPacket();
    }

    @Override
    public Location getLocation() {
        if (shouldUpdateLocation() && canUpdateLocation()) {
            updateLocation();
        }
        return location;
    }

}
