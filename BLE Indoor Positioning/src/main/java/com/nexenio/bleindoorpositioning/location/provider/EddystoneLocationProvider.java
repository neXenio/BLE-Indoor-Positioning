package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;

/**
 * Created by steppschuh on 16.11.17.
 */

public class EddystoneLocationProvider<B extends Eddystone> extends BeaconLocationProvider<B> {

    public EddystoneLocationProvider(B beacon) {
        super(beacon);
    }

    @Override
    public void updateLocation() {
        //AdvertisingPacket advertisingPacket = beacon.getLatestAdvertisingPacket();
        //location = new Location();
        // TODO: get location from advertising packets
    }

}
