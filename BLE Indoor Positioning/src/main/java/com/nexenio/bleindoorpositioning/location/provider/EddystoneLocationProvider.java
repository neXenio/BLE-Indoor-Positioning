package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.ble.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.Eddystone;
import com.nexenio.bleindoorpositioning.location.Location;

/**
 * Created by steppschuh on 16.11.17.
 */

public class EddystoneLocationProvider extends BeaconLocationProvider<Eddystone> {

    public EddystoneLocationProvider(Eddystone beacon) {
        super(beacon);
    }

    @Override
    public Location updateLocation() {
        if (!beacon.hasAnyAdvertisingPacket()) {
            return null;
        }
        AdvertisingPacket advertisingPacket = beacon.getLatestAdvertisingPacket();
        Location location = new Location();
        // TODO: get location from advertising packets
        return location;
    }

}
