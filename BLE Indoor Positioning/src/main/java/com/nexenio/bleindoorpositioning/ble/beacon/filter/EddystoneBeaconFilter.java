package com.nexenio.bleindoorpositioning.ble.beacon.filter;

import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;

import java.util.Collection;
import java.util.List;

public class EddystoneBeaconFilter<B extends Eddystone> extends GenericBeaconFilter<B> {
    @Override
    public boolean matches(B beacon) {
        return super.matches(beacon);
    }

    @Override
    public List<B> getMatches(Collection<B> beacons) {
        return super.getMatches(beacons);
    }
}
