package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconViewFragment;

import java.util.ArrayList;

public class BeaconMapFragment extends BeaconViewFragment {

    private BeaconMap beaconMap;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_beacon_map;
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                // TODO: remove artificial noise
                //location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
                //location.setLongitude(location.getLongitude() + Math.random() * 0.0002);

                beaconMap.setDeviceLocation(location);
                beaconMap.fitToCurrentLocations();
            }
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return new BeaconUpdateListener() {
            @Override
            public void onBeaconUpdated(Beacon beacon) {
                beaconMap.setBeacons(new ArrayList<>(beaconManager.getBeaconMap().values()));
            }
        };
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = super.onCreateView(inflater, container, savedInstanceState);
        beaconMap = inflatedView.findViewById(R.id.beaconMap);
        //beaconMap.setBeacons(createTestBeacons());
        beaconMap.setBeacons(new ArrayList<>(beaconManager.getBeaconMap().values()));
        return inflatedView;
    }

}
