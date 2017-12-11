package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;


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

public class BeaconChartFragment extends BeaconViewFragment {

    private BeaconChart beaconChart;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_beacon_chart;
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                // TODO: remove artificial noise
                //location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
                //location.setLongitude(location.getLongitude() + Math.random() * 0.0002);
                beaconChart.setDeviceLocation(location);
            }
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return new BeaconUpdateListener() {
            @Override
            public void onBeaconUpdated(Beacon beacon) {
                beaconChart.setBeacons(new ArrayList<>(beaconManager.getBeaconMap().values()));
            }
        };
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = super.onCreateView(inflater, container, savedInstanceState);
        beaconChart = inflatedView.findViewById(R.id.beaconChart);
        //beaconChart.setBeacons(createTestBeacons());
        beaconChart.setBeacons(new ArrayList<>(beaconManager.getBeaconMap().values()));
        return inflatedView;
    }

}
