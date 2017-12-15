package com.nexenio.bleindoorpositioningdemo.ui.beaconview;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
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

public abstract class BeaconViewFragment extends Fragment {

    protected BeaconManager beaconManager = BeaconManager.getInstance();
    protected LocationListener deviceLocationListener;
    protected BeaconUpdateListener beaconUpdateListener;

    protected CoordinatorLayout coordinatorLayout;

    @ColorUtil.ColoringMode
    protected int coloringMode = ColorUtil.COLORING_MODE_INSTANCES;

    public BeaconViewFragment() {
        deviceLocationListener = createDeviceLocationListener();
        beaconUpdateListener = createBeaconUpdateListener();
    }

    protected abstract LocationListener createDeviceLocationListener();

    protected abstract BeaconUpdateListener createBeaconUpdateListener();

    @LayoutRes
    protected abstract int getLayoutResourceId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(getLayoutResourceId(), container, false);
        coordinatorLayout = inflatedView.findViewById(R.id.coordinatorLayout);
        return inflatedView;
    }

    @CallSuper
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        IndoorPositioning.registerLocationListener(deviceLocationListener);
        AndroidLocationProvider.registerLocationListener(deviceLocationListener);
        AndroidLocationProvider.requestLastKnownLocation();
        BeaconManager.registerBeaconUpdateListener(beaconUpdateListener);
    }

    @CallSuper
    @Override
    public void onDetach() {
        IndoorPositioning.unregisterLocationListener(deviceLocationListener);
        AndroidLocationProvider.unregisterLocationListener(deviceLocationListener);
        BeaconManager.unregisterBeaconUpdateListener(beaconUpdateListener);
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.beacon_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_color_by_instance: {
                onColoringModeSelected(ColorUtil.COLORING_MODE_INSTANCES, item);
                return true;
            }
            case R.id.menu_color_by_type: {
                onColoringModeSelected(ColorUtil.COLORING_MODE_TYPES, item);
                return true;
            }
            case R.id.menu_color_by_property: {
                onColoringModeSelected(ColorUtil.COLORING_MODE_PROPERTIES, item);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onColoringModeSelected(@ColorUtil.ColoringMode int coloringMode, MenuItem menuItem) {
        menuItem.setChecked(true);
        this.coloringMode = coloringMode;
        Log.d("Coloring Mode", "" + coloringMode);
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
