package com.nexenio.bleindoorpositioningdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.Eddystone;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.bluetooth.BluetoothClient;
import com.nexenio.bleindoorpositioningdemo.location.AndroidLocationProvider;
import com.nexenio.bleindoorpositioningdemo.location.TestLocations;
import com.nexenio.bleindoorpositioningdemo.ui.BeaconMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BeaconMap beaconMap;
    private LocationListener deviceLocationListener;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        beaconMap = findViewById(R.id.beaconMap);
        beaconMap.setBeacons(createTestBeacons());

        // setup location
        AndroidLocationProvider.initialize(this);
        deviceLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                // TODO: remove artificial noise
                //location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
                //location.setLongitude(location.getLongitude() + Math.random() * 0.0002);

                beaconMap.setDeviceLocation(location);
                beaconMap.fitToCurrentLocations();
            }
        };

        // setup bluetooth
        BluetoothClient.initialize(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // observe location
        if (!AndroidLocationProvider.hasLocationPermission(this)) {
            AndroidLocationProvider.requestLocationPermission(this);
        } else if (!AndroidLocationProvider.isLocationEnabled(this)) {
            requestLocationServices();
        }
        AndroidLocationProvider.registerLocationListener(deviceLocationListener);
        AndroidLocationProvider.requestLastKnownLocation();

        // observe bluetooth
        if (!BluetoothClient.isBluetoothEnabled()) {
            requestBluetooth();
        }
        BluetoothClient.startScanning();
    }

    @Override
    protected void onPause() {
        // stop observing location
        AndroidLocationProvider.unregisterLocationListener(deviceLocationListener);

        // stop observing bluetooth
        BluetoothClient.stopScanning();

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AndroidLocationProvider.REQUEST_CODE_LOCATION_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted");
                    AndroidLocationProvider.startRequestingLocationUpdates();
                } else {
                    Log.d(TAG, "Location permission not granted. Wut?");
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothClient.REQUEST_CODE_ENABLE_BLUETOOTH: {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Bluetooth enabled, starting to scan");
                    BluetoothClient.startScanning();
                } else {
                    Log.d(TAG, "Bluetooth not enabled, invoking new request");
                    BluetoothClient.requestBluetoothEnabling(this);
                }
                break;
            }
        }
    }

    private void requestLocationServices() {
        Snackbar snackbar = Snackbar.make(
                coordinatorLayout,
                R.string.error_location_disabled,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(R.string.action_enable, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidLocationProvider.requestLocationEnabling(MainActivity.this);
            }
        });
        snackbar.show();
    }

    private void requestBluetooth() {
        Snackbar snackbar = Snackbar.make(
                coordinatorLayout,
                R.string.error_bluetooth_disabled,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(R.string.action_enable, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothClient.requestBluetoothEnabling(MainActivity.this);
            }
        });
        snackbar.show();
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
