package com.nexenio.bleindoorpositioning.ble.beacon.filter;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by steppschuh on 19.12.17.
 */

public class IBeaconFilter<B extends IBeacon> extends GenericBeaconFilter<B> {

    protected final List<UUID> proximityUuids = new ArrayList<>();
    protected int major;
    protected int minor;

    protected boolean matchProximityUuid;
    protected boolean matchMajor;
    protected boolean matchMinor;

    public IBeaconFilter() {
    }

    public IBeaconFilter(UUID... proximityUuids) {
        this(Arrays.asList(proximityUuids));
    }

    public IBeaconFilter(List<UUID> proximityUuids) {
        this.proximityUuids.addAll(proximityUuids);
        matchProximityUuid = true;
    }

    @Override
    public boolean canMatch(Beacon beacon) {
        return super.canMatch(beacon) && beacon instanceof IBeacon;
    }

    @Override
    public boolean matches(B beacon) {
        if (!super.matches(beacon)) {
            return false;
        }
        if (matchProximityUuid) {
            boolean uuidMatches = false;
            synchronized (proximityUuids) {
                for (UUID proximityUuid : proximityUuids) {
                    if (proximityUuid.equals(beacon.getProximityUuid())) {
                        uuidMatches = true;
                        break;
                    }
                }
            }
            if (!uuidMatches) {
                return false;
            }
        }
        if (matchMajor) {
            if (major != beacon.getMajor()) {
                return false;
            }
        }
        if (matchMinor) {
            if (minor != beacon.getMinor()) {
                return false;
            }
        }
        return true;
    }

    /*
        Getter & Setter
     */

    public List<UUID> getProximityUuids() {
        return proximityUuids;
    }

    public void setProximityUuids(UUID... proximityUuids) {
        setProximityUuids(Arrays.asList(proximityUuids));
    }

    public void setProximityUuids(List<UUID> proximityUuids) {
        synchronized (this.proximityUuids) {
            this.proximityUuids.clear();
            this.proximityUuids.addAll(proximityUuids);
            matchProximityUuid = true;
        }
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
        matchMajor = true;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
        matchMinor = true;
    }

    public boolean shouldMatchProximityUuid() {
        return matchProximityUuid;
    }

    public void setMatchProximityUuid(boolean matchProximityUuid) {
        this.matchProximityUuid = matchProximityUuid;
    }

    public boolean shouldMatchMajor() {
        return matchMajor;
    }

    public void setMatchMajor(boolean matchMajor) {
        this.matchMajor = matchMajor;
    }

    public boolean shouldMatchMinor() {
        return matchMinor;
    }

    public void setMatchMinor(boolean matchMinor) {
        this.matchMinor = matchMinor;
    }

}
