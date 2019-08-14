package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconManager;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.ble.beacon.filter.GenericBeaconFilter;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconViewFragment;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.ColorUtil;

import java.util.ArrayList;

public class BeaconChartFragment extends BeaconViewFragment {

    private BeaconChart beaconChart;
    private boolean rssiFilterView;

    public BeaconChartFragment() {
        this(false);
    }

    @SuppressLint("ValidFragment")
    public BeaconChartFragment(boolean rssiFilterView) {
        super();
        this.rssiFilterView = rssiFilterView;
        if (this.rssiFilterView) {
            beaconFilters.add(createClosestBeaconFilter());
        } else {
            beaconFilters.add(uuidFilter);
        }
    }

    public GenericBeaconFilter createClosestBeaconFilter() {
        return new GenericBeaconFilter() {

            @Override
            public boolean matches(Beacon beacon) {
                if (BeaconManager.getInstance().getClosestBeacon().equals(beacon)) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    protected int getLayoutResourceId() {
        if (rssiFilterView) {
            return R.layout.fragment_rssi_filter_chart;
        } else {
            return R.layout.fragment_beacon_chart;
        }
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
                beaconChart.setBeacons(getBeacons());
            }
        };
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = super.onCreateView(inflater, container, savedInstanceState);
        beaconChart = inflatedView.findViewById(R.id.beaconChart);
        beaconChart.setBeacons(new ArrayList<>(BeaconManager.getInstance().getBeaconMap().values()));
        return inflatedView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.beacon_chart_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_value_rssi: {
                onValueTypeSelected(BeaconChart.VALUE_TYPE_RSSI, item);
                return true;
            }
            case R.id.menu_value_rssi_filtered: {
                onValueTypeSelected(BeaconChart.VALUE_TYPE_RSSI_FILTERED, item);
                return true;
            }
            case R.id.menu_value_distance: {
                onValueTypeSelected(BeaconChart.VALUE_TYPE_DISTANCE, item);
                return true;
            }
            case R.id.menu_value_frequency: {
                onValueTypeSelected(BeaconChart.VALUE_TYPE_FREQUENCY, item);
                return true;
            }
            case R.id.menu_value_variance: {
                onValueTypeSelected(BeaconChart.VALUE_TYPE_VARIANCE, item);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onValueTypeSelected(@BeaconChart.ValueType int valueType, MenuItem menuItem) {
        menuItem.setChecked(true);
        beaconChart.setValueType(valueType);
    }

    @Override
    protected void onColoringModeSelected(@ColorUtil.ColoringMode int coloringMode, MenuItem menuItem) {
        super.onColoringModeSelected(coloringMode, menuItem);
        beaconChart.setColoringMode(coloringMode);
    }

}
