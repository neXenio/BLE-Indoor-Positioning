package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;

/**
 * Created by steppschuh on 16.11.17.
 */

public abstract class IBeaconLocationProvider<B extends IBeacon> extends BeaconLocationProvider<B> {

    public IBeaconLocationProvider(B beacon) {
        super(beacon);
    }

}
