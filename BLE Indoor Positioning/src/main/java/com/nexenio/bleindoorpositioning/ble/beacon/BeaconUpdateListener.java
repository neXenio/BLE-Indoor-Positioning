package com.nexenio.bleindoorpositioning.ble.beacon;

/**
 * Created by steppschuh on 11.12.17.
 */

public interface BeaconUpdateListener<B extends Beacon> {

    void onBeaconUpdated(B beacon);

}
