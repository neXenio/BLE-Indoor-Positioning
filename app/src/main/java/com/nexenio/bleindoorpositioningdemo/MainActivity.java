package com.nexenio.bleindoorpositioningdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nexenio.bleindoorpositioning.ble.Beacon;
import com.nexenio.bleindoorpositioning.ble.Eddystone;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onPause() {
        super.onPause();
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
