package com.nexenio.bleindoorpositioningdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.ble.Eddystone;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSIONS = 1;
    private BeaconMap beaconMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconMap = findViewById(R.id.beaconMap);
        beaconMap.setBeacons(createTestBeacons());
        beaconMap.setDeviceLocation(TestLocations.GENDAMENMARKT_COURT_CENTER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestDeviceLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    private void requestDeviceLocationUpdates() {
        if (hasLocationPermission()) {
            requestLocationPermission();
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(android.location.Location location) {
                Location deviceLocation = new Location();
                deviceLocation.setLatitude(location.getLatitude());
                deviceLocation.setLongitude(location.getLongitude());
                deviceLocation.setAltitude(deviceLocation.getAltitude());
                beaconMap.setDeviceLocation(deviceLocation);
                beaconMap.fitToCurrentLocations();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE_LOCATION_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestDeviceLocationUpdates();
            } else {
                requestLocationPermission();
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
        return beacon;
    }

}
