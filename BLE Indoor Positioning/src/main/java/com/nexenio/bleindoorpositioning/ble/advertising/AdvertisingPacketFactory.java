package com.nexenio.bleindoorpositioning.ble.advertising;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steppschuh on 02.02.18.
 */

public abstract class AdvertisingPacketFactory<AP extends AdvertisingPacket> {

    protected Map<Class<AP>, AdvertisingPacketFactory<AP>> subFactoryMap = new HashMap<>();
    protected Class<AP> packetClass;

    abstract boolean canCreateAdvertisingPacket(byte[] advertisingData);

    protected boolean canCreateAdvertisingPacketWithDescendants(byte[] advertisingData) {
        for (AdvertisingPacketFactory<AP> advertisingPacketFactory : subFactoryMap.values()) {
            if (advertisingPacketFactory.canCreateAdvertisingPacketWithDescendants(advertisingData)) {
                return true;
            }
        }
        return canCreateAdvertisingPacket(advertisingData);
    }

    abstract AP createAdvertisingPacket(byte[] advertisingData);

    protected AP createAdvertisingPacketWithDescendants(byte[] advertisingData) {
        for (AdvertisingPacketFactory<AP> advertisingPacketFactory : subFactoryMap.values()) {
            if (advertisingPacketFactory.canCreateAdvertisingPacketWithDescendants(advertisingData)) {
                return advertisingPacketFactory.createAdvertisingPacket(advertisingData);
            }
        }
        return createAdvertisingPacket(advertisingData);
    }

    public AdvertisingPacketFactory<AP> getAdvertisingFactory(Class<AP> advertisingPacketClass) {
        if (!packetClass.isAssignableFrom(advertisingPacketClass)) {
            return null;
        }
        if (subFactoryMap.containsKey(advertisingPacketClass)) {
            return subFactoryMap.get(advertisingPacketClass);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(advertisingPacketClass)) {
                    return classAdvertisingPacketFactoryEntry.getValue().getAdvertisingFactory(advertisingPacketClass);
                }
            }
            return null;
        }
    }

    public <F extends AdvertisingPacketFactory<AP>> void addAdvertisingFactory(Class<AP> advertisingPacketClass, F factory) {
        if (!packetClass.isAssignableFrom(advertisingPacketClass)) {
            return;
        }
        if (AdvertisingPacket.class == advertisingPacketClass.getSuperclass() && !subFactoryMap.containsKey(advertisingPacketClass)) {
            subFactoryMap.put(advertisingPacketClass, factory);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(advertisingPacketClass)) {
                    classAdvertisingPacketFactoryEntry.getValue().addAdvertisingFactory(advertisingPacketClass, factory);
                }
            }
        }
    }

    public void removeAdvertisingFactory(Class<AP> advertisingPacketClass) {
        if (!packetClass.isAssignableFrom(advertisingPacketClass)) {
            return;
        }
        if (AdvertisingPacket.class == advertisingPacketClass.getSuperclass() && subFactoryMap.containsKey(advertisingPacketClass)) {
            subFactoryMap.remove(advertisingPacketClass);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(advertisingPacketClass)) {
                    classAdvertisingPacketFactoryEntry.getValue().removeAdvertisingFactory(advertisingPacketClass);
                }
            }
        }
    }

}
