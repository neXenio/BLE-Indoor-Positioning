package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.IBeacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconViewFragment;

import java.util.ArrayList;
import java.util.List;

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
            public void onBeaconUpdated(Beacon updatedBeacon) {
                List<Beacon> beacons = new ArrayList<>();
                for (Beacon beacon : beaconManager.getBeaconMap().values()) {
                    if (shouldRenderBeacon(beacon)) {
                        beacons.add(beacon);
                    }
                }
                beaconChart.setBeacons(beacons);
            }
        };
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = super.onCreateView(inflater, container, savedInstanceState);
        beaconChart = inflatedView.findViewById(R.id.beaconChart);
        beaconChart.setBeacons(new ArrayList<>(beaconManager.getBeaconMap().values()));
        return inflatedView;
    }

    protected boolean shouldRenderBeacon(@NonNull Beacon beacon) {
        if (!(beacon instanceof IBeacon)) {
            return false;
        }
        String proximityUuid = ((IBeacon) beacon).getProximityUuid().toString();
        Log.w(this.getClass().getSimpleName(), proximityUuid);
        if (!"acfd065e-c3c0-11e3-9bbe-1a514932ac01".equals(proximityUuid)) {
            // TODO: remove debug filter
            return false;
        }
        return true;
    }

}
