package com.nexenio.bleindoorpositioningdemo.ui.beaconview.radar;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IBeaconFilter;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.location.AndroidLocationProvider;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconViewFragment;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.radar.BeaconRadar;

import java.util.UUID;

public class BeaconRadarFragment extends BeaconViewFragment {

    private BeaconRadar beaconRadar;

    public BeaconRadarFragment() {
        super();
        IBeaconFilter uuidFilter = new IBeaconFilter() {

            private UUID legacyUuid = UUID.fromString("acfd065e-c3c0-11e3-9bbe-1a514932ac01");
            private UUID indoorPositioningUuid = UUID.fromString("03253fdd-55cb-44c2-a1eb-80c8355f8291");

            @Override
            public boolean matches(IBeacon beacon) {
                if (legacyUuid.equals(beacon.getProximityUuid())) {
                    return true;
                }
                if (indoorPositioningUuid.equals(beacon.getProximityUuid())) {
                    return true;
                }
                return false;
            }
        };
        beaconFilters.add(uuidFilter);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_beacon_radar;
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                if (locationProvider == IndoorPositioning.getInstance()) {
                    beaconRadar.setDeviceLocation(location);
                    beaconRadar.fitToCurrentLocations();
                } else if (locationProvider == AndroidLocationProvider.getInstance()) {
                    // TODO: remove artificial noise
                    //location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
                    //location.setLongitude(location.getLongitude() + Math.random() * 0.0002);
                }
            }
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return new BeaconUpdateListener() {
            @Override
            public void onBeaconUpdated(Beacon beacon) {
                beaconRadar.setBeacons(getBeacons());
            }
        };
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = super.onCreateView(inflater, container, savedInstanceState);
        beaconRadar = inflatedView.findViewById(R.id.beaconRadar);
        beaconRadar.setBeacons(getBeacons());
        return inflatedView;
    }

}
