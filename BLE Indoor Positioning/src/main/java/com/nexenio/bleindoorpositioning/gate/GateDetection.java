package com.nexenio.bleindoorpositioning.gate;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.BeaconFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IBeaconFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by steppschuh on 21.12.17.
 */

public class GateDetection implements BeaconUpdateListener {

    private static GateDetection instance;
    private BeaconFilter gateDetectionBeaconFilter;

    private Map<Integer, GateGroup> gateGroups = new HashMap<>();
    private GateGroup closestGateGroup;
    private Gate closestGate;
    private float closestGateDistance = Float.MAX_VALUE;

    private Set<ClosestGateChangeListener> closestGateChangeListeners = new HashSet<>();

    private GateDetection() {
        gateDetectionBeaconFilter = new IBeaconFilter() {

            private UUID gateDetectionUuid = UUID.fromString("f175c9a8-d51c-4d25-8449-4d3d340d1067");

            @Override
            public boolean matches(IBeacon beacon) {
                if (!gateDetectionUuid.equals(beacon.getProximityUuid())) {
                    return false;
                }
                return true;
            }
        };
    }

    public static GateDetection getInstance() {
        if (instance == null) {
            instance = new GateDetection();
            BeaconManager.registerBeaconUpdateListener(instance);
        }
        return instance;
    }

    @Override
    public void onBeaconUpdated(Beacon beacon) {
        if (!gateDetectionBeaconFilter.matches(beacon)) {
            return;
        }

        IBeacon gateBeacon = (IBeacon) beacon;
        int gateGroupIndex = getGateGroupIndex(gateBeacon);
        int gateIndex = getGateIndex(gateBeacon);

        GateGroup gateGroup = gateGroups.get(gateGroupIndex);
        if (gateGroup == null) {
            gateGroup = new GateGroup(gateGroupIndex);
            gateGroups.put(gateGroupIndex, gateGroup);
        }

        Gate gate = gateGroup.getGates().get(gateIndex);
        if (gate == null) {
            gate = new Gate(gateIndex);
            gateGroup.getGates().put(gateIndex, gate);
        }

        if (gateBeacon.getMinor() % 2 == 0) {
            gate.setLeftBeacon(gateBeacon);
        } else {
            gate.setRightBeacon(gateBeacon);
        }

        float gateDistance = gate.getClosestDistance();
        if (gateDistance < closestGateDistance - 0.25 || gate == closestGate) {
            onClosestGateDistanceChanged(gateGroup, gate, gateDistance);
        }
    }

    public void onClosestGateDistanceChanged(GateGroup gateGroup, Gate gate, float distance) {
        closestGateDistance = distance;
        for (ClosestGateChangeListener closestGateChangeListener : closestGateChangeListeners) {
            closestGateChangeListener.onClosestGateDistanceChanged(gateGroup, gate, distance);
        }
        if (closestGate != gate) {
            onClosestGateChanged(gateGroup, gate, distance);
        }
    }

    public void onClosestGateChanged(GateGroup gateGroup, Gate gate, float distance) {
        closestGate = gate;
        for (ClosestGateChangeListener closestGateChangeListener : closestGateChangeListeners) {
            closestGateChangeListener.onClosestGateChanged(gateGroup, gate, distance);
        }
        if (closestGateGroup != gateGroup) {
            onClosestGateGroupChanged(gateGroup, gate, distance);
        }
    }

    public void onClosestGateGroupChanged(GateGroup gateGroup, Gate gate, float distance) {
        closestGateGroup = gateGroup;
        for (ClosestGateChangeListener closestGateChangeListener : closestGateChangeListeners) {
            closestGateChangeListener.onClosestGateGroupChanged(gateGroup, gate, distance);
        }
    }

    private static int getGateGroupIndex(IBeacon iBeacon) {
        return iBeacon.getMajor();
    }

    private static int getGateIndex(IBeacon iBeacon) {
        return (int) Math.floor(iBeacon.getMinor() / 2);
    }

    public static boolean registerClosestGateChangeListener(ClosestGateChangeListener closestGateChangeListener) {
        return getInstance().closestGateChangeListeners.add(closestGateChangeListener);
    }

    public static boolean unregisterClosestGateChangeListener(ClosestGateChangeListener closestGateChangeListener) {
        return getInstance().closestGateChangeListeners.remove(closestGateChangeListener);
    }

}
