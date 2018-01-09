package com.nexenio.bleindoorpositioning.gate;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;

import java.util.concurrent.TimeUnit;

/**
 * Created by steppschuh on 21.12.17.
 */

public class Gate {

    private int index;
    private Location location;
    private Beacon leftBeacon;
    private Beacon rightBeacon;

    public Gate(int index) {
        this.index = index;
    }

    public boolean isInRange() {
        if (leftBeacon == null || rightBeacon == null) {
            return false;
        }
        long minimumTimestamp = System.currentTimeMillis() - 2000;
        if (!leftBeacon.hasBeenSeenSince(minimumTimestamp)) {
            return false;
        }
        if (!rightBeacon.hasBeenSeenSince(minimumTimestamp)) {
            return false;
        }
        return true;
    }

    public Beacon getClosestBeacon() {
        return getClosestBeacon(2, TimeUnit.SECONDS);
    }

    public Beacon getClosestBeacon(long amount, TimeUnit timeUnit) {
        if (leftBeacon != null && rightBeacon != null) {
            return (leftBeacon.getMeanRssi(amount, timeUnit) > rightBeacon.getMeanRssi(amount, timeUnit)) ? leftBeacon : rightBeacon;
        } else if (leftBeacon == null && rightBeacon != null) {
            return rightBeacon;
        } else if (leftBeacon != null && rightBeacon == null) {
            return leftBeacon;
        } else {
            return null;
        }
    }

    public float getClosestDistance() {
        Beacon closestBeacon = getClosestBeacon();
        if (closestBeacon == null) {
            return Float.MAX_VALUE;
        }
        return closestBeacon.getDistance();
    }

    public float getMeanDistance() {
        float distancesSum = 0;
        int distancesCount = 0;
        if (leftBeacon != null) {
            distancesSum += leftBeacon.getDistance();
            distancesCount++;
        }
        if (rightBeacon != null) {
            distancesSum += rightBeacon.getDistance();
            distancesCount++;
        }
        if (distancesCount == 0) {
            return Float.MAX_VALUE;
        } else {
            return distancesSum / (float) distancesCount;
        }
    }

    @Override
    public String toString() {
        return "Gate with index " + index
                + "\n- Left beacon: " + leftBeacon
                + "\n- Right beacon: " + rightBeacon;
    }

    /*
        Getter & Setter
     */

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Beacon getLeftBeacon() {
        return leftBeacon;
    }

    public void setLeftBeacon(Beacon leftBeacon) {
        this.leftBeacon = leftBeacon;
    }

    public Beacon getRightBeacon() {
        return rightBeacon;
    }

    public void setRightBeacon(Beacon rightBeacon) {
        this.rightBeacon = rightBeacon;
    }

}
