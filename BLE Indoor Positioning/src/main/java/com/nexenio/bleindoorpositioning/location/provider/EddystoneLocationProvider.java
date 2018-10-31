package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;

/**
 * Created by steppschuh on 16.11.17.
 */

public abstract class EddystoneLocationProvider<B extends Eddystone> extends BeaconLocationProvider<B> {

    public EddystoneLocationProvider(B beacon) {
        super(beacon);
    }

}
