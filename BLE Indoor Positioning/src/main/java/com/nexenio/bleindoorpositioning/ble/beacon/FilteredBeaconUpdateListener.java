package com.nexenio.bleindoorpositioning.ble.beacon;

import com.nexenio.bleindoorpositioning.ble.beacon.filter.BeaconFilter;

/**
 * An update listener that automatically filters updated beacons based on a given {@link
 * BeaconFilter}.
 */

public abstract class FilteredBeaconUpdateListener<B extends Beacon> implements BeaconUpdateListener<B> {

    /**
     * The filter that will be used to decide if {@link #onMatchingBeaconUpdated(Beacon)} or {@link
     * #onNonMatchingBeaconUpdated(Beacon)} will be called.
     */
    protected BeaconFilter<B> beaconFilter;

    public FilteredBeaconUpdateListener(BeaconFilter<B> beaconFilter) {
        this.beaconFilter = beaconFilter;
    }

    @Override
    public void onBeaconUpdated(B beacon) {
        if (beaconFilter.matches(beacon)) {
            onMatchingBeaconUpdated(beacon);
        } else {
            onNonMatchingBeaconUpdated(beacon);
        }
    }

    /**
     * Will be called when {@link #onBeaconUpdated(Beacon)} gets called with a beacon that matches
     * the current {@link #beaconFilter}.
     */
    public abstract void onMatchingBeaconUpdated(B beacon);

    /**
     * Will be called when {@link #onBeaconUpdated(Beacon)} gets called with a beacon that doesn't
     * match the current {@link #beaconFilter}.
     */
    public void onNonMatchingBeaconUpdated(B beacon) {
        // usually not of any interest
    }

    /*
        Getter & Setter
     */

    public BeaconFilter<B> getBeaconFilter() {
        return beaconFilter;
    }

    public void setBeaconFilter(BeaconFilter<B> beaconFilter) {
        this.beaconFilter = beaconFilter;
    }

}
