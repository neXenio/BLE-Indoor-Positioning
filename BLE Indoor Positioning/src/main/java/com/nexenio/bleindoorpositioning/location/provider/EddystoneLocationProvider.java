package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;
import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 16.11.17.
 */

public class EddystoneLocationProvider extends BeaconLocationProvider<Eddystone> {

    public EddystoneLocationProvider(Eddystone beacon) {
        super(beacon);
    }

    @Override
    public void updateLocation() {
        AdvertisingPacket advertisingPacket = beacon.getLatestAdvertisingPacket();
        location = new Location();
        // TODO: get location from advertising packets
    }

}
