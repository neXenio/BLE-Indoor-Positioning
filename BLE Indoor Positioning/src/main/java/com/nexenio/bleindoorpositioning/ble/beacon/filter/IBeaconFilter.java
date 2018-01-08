package com.nexenio.bleindoorpositioning.ble.beacon.filter;

import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;

import java.util.UUID;

/**
 * Created by steppschuh on 19.12.17.
 */

public class IBeaconFilter extends GenericBeaconFilter<IBeacon> {

    protected UUID proximityUuid;
    protected int major;
    protected int minor;

    protected boolean matchProximityUuid;
    protected boolean matchMajor;
    protected boolean matchMinor;

    @Override
    public boolean matches(IBeacon beacon) {
        if (!super.matches(beacon)) {
            return false;
        }
        if (matchProximityUuid) {
            if (!proximityUuid.equals(beacon.getProximityUuid())) {
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

    public UUID getProximityUuid() {
        return proximityUuid;
    }

    public void setProximityUuid(UUID proximityUuid) {
        this.proximityUuid = proximityUuid;
        matchProximityUuid = true;
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
