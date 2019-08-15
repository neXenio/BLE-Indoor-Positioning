package com.nexenio.bleindoorpositioning.ble.beacon.filter;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;

import java.util.Collection;
import java.util.List;

/**
 * Created by steppschuh on 19.12.17.
 */

public interface BeaconFilter<B extends Beacon> {

    boolean canMatch(Beacon beacon);

    boolean matches(B beacon);

    List<B> getMatches(Collection<B> beacons);

}
