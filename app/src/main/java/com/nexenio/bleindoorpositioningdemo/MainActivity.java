package com.nexenio.bleindoorpositioningdemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.ble.Eddystone;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BeaconMap beaconMap;
    private LocationListener deviceLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconMap = findViewById(R.id.beaconMap);
        beaconMap.setBeacons(createTestBeacons());

        AndroidLocationProvider.initialize(this);
        deviceLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                // TODO: remove artificial noise
                location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
                location.setLongitude(location.getLongitude() + Math.random() * 0.0002);

                beaconMap.setDeviceLocation(location);
                beaconMap.fitToCurrentLocations();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AndroidLocationProvider.hasLocationPermission(this)) {
            AndroidLocationProvider.requestLocationPermission(this);
        }
        AndroidLocationProvider.registerLocationListener(deviceLocationListener);
        AndroidLocationProvider.requestLastKnownLocation();
    }

    @Override
    protected void onPause() {
        AndroidLocationProvider.unregisterLocationListener(deviceLocationListener);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AndroidLocationProvider.REQUEST_CODE_LOCATION_PERMISSIONS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted");
                    AndroidLocationProvider.startRequestingLocationUpdates();
                } else {
                    Log.d(TAG, "Location permission not granted. Wut?");
                    AndroidLocationProvider.requestLocationPermission(this);
                }
                break;
            }
        }
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
        beacon.setTransmissionPower(-8);
        beacon.setRssi(-80);
        beacon.setCalibratedRssi(-37);
        return beacon;
    }

}
