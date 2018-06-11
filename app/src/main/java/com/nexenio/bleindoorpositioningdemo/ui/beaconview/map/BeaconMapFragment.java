package com.nexenio.bleindoorpositioningdemo.ui.beaconview.map;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexenio.bleindoorpositioning.IndoorPositioning;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.ble.beacon.BeaconUpdateListener;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.location.AndroidLocationProvider;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconViewFragment;

public class BeaconMapFragment extends BeaconViewFragment {

    private BeaconMap beaconMap;

    public BeaconMapFragment() {
        super();
        beaconFilters.add(uuidFilter);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_beacon_map;
    }

    @Override
    protected LocationListener createDeviceLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                if (locationProvider == IndoorPositioning.getInstance()) {
                    beaconMap.setDeviceLocation(location);
                    beaconMap.setPredictedDeviceLocation(IndoorPositioning.getLocationPredictor().getLocation());
                    beaconMap.fitToCurrentLocations();
                } else if (locationProvider == AndroidLocationProvider.getInstance()) {
                    // TODO: remove artificial noise
                    //location.setLatitude(location.getLatitude() + Math.random() * 0.0002);
                    //location.setLongitude(location.getLongitude() + Math.random() * 0.0002);
                }
            }
        };
    }

    @Override
    protected BeaconUpdateListener createBeaconUpdateListener() {
        return new BeaconUpdateListener() {
            @Override
            public void onBeaconUpdated(Beacon beacon) {
                beaconMap.setBeacons(getBeacons());
            }
        };
    }

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = super.onCreateView(inflater, container, savedInstanceState);
        beaconMap = inflatedView.findViewById(R.id.beaconMap);
        beaconMap.setBeacons(getBeacons());

        Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.mipmap.map_view_background);
        Location centerLocation = new Location(52.512390301005595, 13.39077285305359);
        float metersScale = 10.95f / 717.1f;
        float scale = (backgroundImage.getWidth() / 36.0f);
        float bearing = 353.84f;

        beaconMap.setBackgroundImage(backgroundImage);
        beaconMap.setBackgroundCenterLocation(centerLocation);
        beaconMap.setBackgroundMetersPerPixel(scale);
        beaconMap.setBackgroundBearing(bearing);

        return inflatedView;
    }

}
