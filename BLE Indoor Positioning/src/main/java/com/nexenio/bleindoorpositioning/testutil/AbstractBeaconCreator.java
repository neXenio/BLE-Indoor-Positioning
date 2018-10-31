package com.nexenio.bleindoorpositioning.testutil;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUtil;

import java.util.concurrent.ExecutionException;

/**
 * Use this class to create beacon with correct advertising packets in your tests.
 *
 * @param <B> Beacon type for which instances will be created
 */
public abstract class AbstractBeaconCreator<B extends Beacon> {

    private Class<B> beaconClass;

    public AbstractBeaconCreator(Class<B> beaconClass) {
        this.beaconClass = beaconClass;
    }

    /**
     * Please note that if you want a single instance of a beacon instead of adding it to a list you
     * need to specify the complete type e.g. {@code IBeacon<IBeaconAdvertisingPacket>}.
     *
     * @param distance Distance for which an rssi will be calculated
     * @return Beacon for the specified beacon type with the specified advertising packet type and
     *         set rssi
     * @throws ExecutionException If reflections fail
     */
    @SuppressWarnings("unchecked")
    public B createBeaconWithAdvertisingPacket(float distance) throws ExecutionException {
        try {
            B beacon = beaconClass.newInstance();
            AdvertisingPacket advertisingPacket = createAdvertisingPacketForBeaconClass(beaconClass);
            int rssi = BeaconUtil.calculateRssiForDistance(beacon, distance);
            advertisingPacket.setRssi(rssi);
            beacon.addAdvertisingPacket(advertisingPacket);
            return beacon;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Create a matching advertising packet for a given beacon class. The reverse process to
     * creating a beacon from a given advertising packet.
     *
     * @param beaconClass Class of the beacon type for which the matching advertising packet should
     *                    be created
     * @return Advertising packet matching the given beacon class
     * @throws InstantiationException if no matching advertising packet could be created
     */
    public abstract AdvertisingPacket createAdvertisingPacketForBeaconClass(Class<B> beaconClass) throws InstantiationException;

}
