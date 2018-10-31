package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.EddystoneAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.advertising.IBeaconAdvertisingPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory that can create instances of {@link Beacon}s based on a specified {@link
 * AdvertisingPacket}.
 *
 * You may extend this factory by using the {@link #addBeaconClass(Class, Class)} method.
 */

public class BeaconFactory {

    /**
     * Holds a mapping of {@link AdvertisingPacket} classes to {@link Beacon} classes.
     */
    private Map<Class<? extends AdvertisingPacket>, Class<? extends Beacon>> beaconClasses = new HashMap<>();

    public BeaconFactory() {
        beaconClasses.put(IBeaconAdvertisingPacket.class, IBeacon.class);
        beaconClasses.put(EddystoneAdvertisingPacket.class, Eddystone.class);
    }

    /**
     * Will create a new instance of a class extending {@link Beacon} that matches the specified
     * {@link AdvertisingPacket}.
     *
     * @param advertisingPacket one of the advertising packets that the desired beacon advertised
     */
    public Beacon createBeacon(AdvertisingPacket advertisingPacket) {
        Class<? extends Beacon> beaconClass = getBeaconClass(advertisingPacket);
        if (beaconClass == null) {
            return null;
        }
        try {
            return beaconClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Will perform a lookup in the {@link #beaconClasses} map.
     */
    public Class<? extends Beacon> getBeaconClass(AdvertisingPacket advertisingPacketClass) {
        return beaconClasses.get(advertisingPacketClass.getClass());
    }

    /**
     * Will update the {@link #beaconClasses} map.
     */
    public void addBeaconClass(Class<? extends AdvertisingPacket> advertisingPacketClass, Class<? extends Beacon> beaconClass) {
        beaconClasses.put(advertisingPacketClass, beaconClass);
    }

    /*
        Getter & Setter
     */

    public Map<Class<? extends AdvertisingPacket>, Class<? extends Beacon>> getBeaconClasses() {
        return beaconClasses;
    }

    public void setBeaconClasses(Map<Class<? extends AdvertisingPacket>, Class<? extends Beacon>> beaconClasses) {
        this.beaconClasses = beaconClasses;
    }

}
