package com.nexenio.bleindoorpositioning.ble.beacon.signal;

import com.nexenio.bleindoorpositioning.ble.beacon.FilteredBeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.IndoorPositioningBeacon;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IndoorPositioningBeaconFilter;

public abstract class IndoorPositioningBeaconUpdateListener extends FilteredBeaconUpdateListener<IndoorPositioningBeacon> {

    public IndoorPositioningBeaconUpdateListener() {
        super(new IndoorPositioningBeaconFilter());
    }

}
