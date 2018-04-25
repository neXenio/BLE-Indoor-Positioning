package com.nexenio.bleindoorpositioning.ble.advertising;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steppschuh on 02.02.18.
 */

public abstract class AdvertisingPacketFactory<AP extends AdvertisingPacket> {

    private Map<Class<AP>, AdvertisingPacketFactory<AP>> subFactoryMap = new HashMap<>();
    private Class<AP> packetClass;

    public AdvertisingPacketFactory(Class<AP> packetClass) {
        this.packetClass = packetClass;
    }

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

    public AdvertisingPacketFactory<AP> getAdvertisingFactory(Class advertisingPacketClass) {
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

    public AdvertisingPacketFactory<AP> getAdvertisingFactory(byte[] advertisingData) {
        if (canCreateAdvertisingPacket(advertisingData)) {
            for (AdvertisingPacketFactory<AP> advertisingPacketFactory : subFactoryMap.values()) {
                if (advertisingPacketFactory.getAdvertisingFactory(advertisingData) != null) {
                    return advertisingPacketFactory;
                }
            }
            return this;
        }
        return null;
    }

    public <F extends AdvertisingPacketFactory<AP>> void addAdvertisingFactory(F factory) {
        if (!packetClass.isAssignableFrom(factory.getPacketClass())) {
            return;
        }
        if (packetClass == factory.getPacketClass().getSuperclass() && !subFactoryMap.containsKey(factory.getPacketClass())) {
            subFactoryMap.put(factory.getPacketClass(), factory);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(factory.getPacketClass())) {
                    classAdvertisingPacketFactoryEntry.getValue().addAdvertisingFactory(factory);
                }
            }
        }
    }

    public void removeAdvertisingFactory(Class<AP> advertisingPacketClass) {
        if (!packetClass.isAssignableFrom(advertisingPacketClass)) {
            return;
        }
        if (packetClass == advertisingPacketClass.getSuperclass() && subFactoryMap.containsKey(advertisingPacketClass)) {
            subFactoryMap.remove(advertisingPacketClass);
        } else {
            for (Map.Entry<Class<AP>, AdvertisingPacketFactory<AP>> classAdvertisingPacketFactoryEntry : subFactoryMap.entrySet()) {
                if (classAdvertisingPacketFactoryEntry.getKey().isAssignableFrom(advertisingPacketClass)) {
                    classAdvertisingPacketFactoryEntry.getValue().removeAdvertisingFactory(advertisingPacketClass);
                }
            }
        }
    }

    public <F extends AdvertisingPacketFactory<AP>> void removeAdvertisingFactory(F factory) {
        removeAdvertisingFactory(factory.getPacketClass());
    }

    public Map<Class<AP>, AdvertisingPacketFactory<AP>> getSubFactoryMap() {
        return subFactoryMap;
    }

    public Class<AP> getPacketClass() {
        return packetClass;
    }

}
