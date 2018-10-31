package com.nexenio.bleindoorpositioningdemo.ui.beaconview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.advertising.IndoorPositioningAdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.BeaconFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.EddystoneBeaconFilter;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.IBeaconFilter;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.location.AndroidLocationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BeaconViewFragment extends Fragment {

    protected BeaconManager beaconManager = BeaconManager.getInstance();
    protected LocationListener deviceLocationListener;
    protected BeaconUpdateListener beaconUpdateListener;
    protected List<BeaconFilter> beaconFilters = new ArrayList<>();

    // TODO: Remove legacy uuid once all beacons are updated
    // protected IBeaconFilter uuidFilter = new IBeaconFilter(IndoorPositioningAdvertisingPacket.INDOOR_POSITIONING_UUID);
//    protected IBeaconFilter uuidFilter = new IBeaconFilter(IndoorPositioningAdvertisingPacket.INDOOR_POSITIONING_UUID, UUID.fromString("acfd065e-c3c0-11e3-9bbe-1a514932ac01"));
    protected EddystoneBeaconFilter uuidFilter = new EddystoneBeaconFilter();

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
        IndoorPositioning.getInstance().setIndoorPositioningBeaconFilter(uuidFilter);
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

    public EddystoneBeaconFilter getUuidFilter() {
        return uuidFilter;
    }

    protected void onColoringModeSelected(@ColorUtil.ColoringMode int coloringMode, MenuItem menuItem) {
        menuItem.setChecked(true);
        this.coloringMode = coloringMode;
    }

    protected List<Beacon> getBeacons() {
        if (beaconFilters.isEmpty()) {
            return new ArrayList<>(beaconManager.getBeaconMap().values());
        }
        List<Beacon> beacons = new ArrayList<>();
        for (Beacon beacon : beaconManager.getBeaconMap().values()) {
            for (BeaconFilter beaconFilter : beaconFilters) {
                if (beaconFilter.matches(beacon)) {
                    beacons.add(beacon);
                    break;
                }
            }
        }
        return beacons;
    }

    public void setUuidFilter(EddystoneBeaconFilter uuidFilter) {
        this.uuidFilter = uuidFilter;
    }
}
