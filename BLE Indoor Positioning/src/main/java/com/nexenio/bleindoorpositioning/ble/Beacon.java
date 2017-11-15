package com.nexenio.bleindoorpositioning.ble;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steppschuh on 15.11.17.
 */

public class Beacon {

    protected UUID uuid;
    protected List<AdvertisingPacket> advertisingPackets;

    public Beacon() {

    }

    public boolean hasAnyAdvertisingPacket() {
        return advertisingPackets != null && !advertisingPackets.isEmpty();
    }

    public AdvertisingPacket getLatestAdvertisingPacket() {
        if (!hasAnyAdvertisingPacket()) {
            return null;
        }
        return advertisingPackets.get(advertisingPackets.size() - 1);
    }

    public void addAdvertisingPacket(AdvertisingPacket advertisingPacket) {
        if (advertisingPackets == null) {
            advertisingPackets = new ArrayList<>();
        }
        advertisingPackets.add(advertisingPacket);
        trimAdvertisingPackets();
    }

    public void trimAdvertisingPackets() {
        if (!hasAnyAdvertisingPacket()) {
            return;
        }
        List<AdvertisingPacket> removableAdvertisingPackets = new ArrayList<>();
        AdvertisingPacket latestAdvertisingPacket = getLatestAdvertisingPacket();
        long minimumPacketTimestamp = System.currentTimeMillis() - AdvertisingPacket.MAXIMUM_PACKET_AGE;
        for (AdvertisingPacket advertisingPacket : advertisingPackets) {
            if (advertisingPacket == latestAdvertisingPacket) {
                // don't remove the latest packet
                continue;
            }
            if (advertisingPacket.getTimestamp() < minimumPacketTimestamp) {
                // mark old packets as removable
                removableAdvertisingPackets.add(advertisingPacket);
            }
        }

        advertisingPackets.removeAll(removableAdvertisingPackets);
    }

}
