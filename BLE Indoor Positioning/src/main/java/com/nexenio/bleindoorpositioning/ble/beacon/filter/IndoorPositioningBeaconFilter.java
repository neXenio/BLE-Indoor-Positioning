package com.nexenio.bleindoorpositioning.ble.beacon.filter;

import java.util.UUID;

public class IndoorPositioningBeaconFilter extends IBeaconFilter {

    public static final UUID INDOOR_POSITIONING_UUID = UUID.fromString("03253fdd-55cb-44c2-a1eb-80c8355f8291");

    public IndoorPositioningBeaconFilter() {
        setProximityUuid(INDOOR_POSITIONING_UUID);
    }

}
