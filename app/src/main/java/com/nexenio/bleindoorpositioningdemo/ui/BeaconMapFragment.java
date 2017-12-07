package com.nexenio.bleindoorpositioningdemo.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.location.AndroidLocationProvider;
import com.nexenio.bleindoorpositioningdemo.location.TestLocations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeaconMapFragment extends Fragment {

    private BeaconManager beaconManager = BeaconManager.getInstance();
    private BeaconMap beaconMap;
    private LocationListener deviceLocationListener;

    private CoordinatorLayout coordinatorLayout;

    public BeaconMapFragment() {
        deviceLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                // TODO: remove artificial noise
                //location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
                //location.setLongitude(location.getLongitude() + Math.random() * 0.0002);

                beaconMap.setDeviceLocation(location);
                beaconMap.setBeacons(new ArrayList<>(beaconManager.getBeaconMap().values()));
                beaconMap.fitToCurrentLocations();
            }
        };
    }

    public static BeaconMapFragment newInstance() {
        BeaconMapFragment fragment = new BeaconMapFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_beacon_map, container, false);
        coordinatorLayout = inflatedView.findViewById(R.id.coordinatorLayout);
        beaconMap = inflatedView.findViewById(R.id.beaconMap);
        //beaconMap.setBeacons(createTestBeacons());
        beaconMap.setBeacons(new ArrayList<>(beaconManager.getBeaconMap().values()));
        return inflatedView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidLocationProvider.registerLocationListener(deviceLocationListener);
        AndroidLocationProvider.requestLastKnownLocation();
    }

    @Override
    public void onDetach() {
        AndroidLocationProvider.unregisterLocationListener(deviceLocationListener);
        super.onDetach();
    }

    private static List<Beacon> createTestBeacons() {
        return new ArrayList<>(Arrays.asList(
                createTestBeacon(TestLocations.GENDAMENMARKT_COURT_TOP_LEFT),
                createTestBeacon(TestLocations.GENDAMENMARKT_COURT_TOP_RIGHT),
                createTestBeacon(TestLocations.GENDAMENMARKT_COURT_BOTTOM_LEFT),
                createTestBeacon(TestLocations.GENDAMENMARKT_COURT_BOTTOM_RIGHT)
        ));
    }

    private static Beacon createTestBeacon(final Location location) {
        Beacon beacon = new Eddystone();
        beacon.setLocationProvider(new LocationProvider() {
            @Override
            public Location getLocation() {
                return location;
            }
        });
        beacon.setTransmissionPower(0);
        beacon.setRssi(-80);
        beacon.setCalibratedRssi(-37);
        return beacon;
    }

}
