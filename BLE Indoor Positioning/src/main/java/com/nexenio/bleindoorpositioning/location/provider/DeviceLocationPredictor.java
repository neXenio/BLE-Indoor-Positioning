package com.nexenio.bleindoorpositioning.location.provider;


import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.BeaconDistanceCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leon on 29.01.18.
 */

public class DeviceLocationPredictor {

    static List<Location> pastDeviceLocations;

    public DeviceLocationPredictor() {
        pastDeviceLocations = new ArrayList<>();
    }

    public void saveCurrentLocation(Location location) {
        int locationWindow = 20;
        pastDeviceLocations.add(location);
        if (pastDeviceLocations.size() > locationWindow){
            pastDeviceLocations.remove(0);
        }
    }

    public Location predictNewLocation(Location deviceCenter) {

        // TODO  what if no movement

        Location predLocation = new Location();

        double sumPastLocationsLat = 0;
        double sumPastLocationsLon = 0;
        for (int i = 0; i < pastDeviceLocations.size(); i++) {
            sumPastLocationsLat =+ pastDeviceLocations.get(i).getLatitude();
            sumPastLocationsLon =+ pastDeviceLocations.get(i).getLongitude();
        }
        double meanPastLocationsLat = sumPastLocationsLat / pastDeviceLocations.size();
        double meanPastLocationsLon = sumPastLocationsLon / pastDeviceLocations.size();

        // TODO estimate heading direction

        //set window to start prediction
        if (pastDeviceLocations.size() == 0) {
            predLocation = deviceCenter;
        } else {
            //predLocation = pastDeviceLocations.get(pastDeviceLocations.size() - 1);
            // TODO actually predict something
            predLocation.setLatitude(deviceCenter.getLatitude() + Math.random() * 0.00002);
            predLocation.setLongitude(deviceCenter.getLongitude() + Math.random() * 0.00002);
        }

        // TODO calc speed of movement
        double distanceToPrediction = BeaconDistanceCalculator.calculateCartesianDistance(deviceCenter.getLatitude(), deviceCenter.getLongitude(), predLocation.getLatitude(), predLocation.getLongitude());

        double distanceCurrentToMean = BeaconDistanceCalculator.calculateCartesianDistance(meanPastLocationsLat,meanPastLocationsLon,deviceCenter.getLatitude(),deviceCenter.getLongitude());
        System.out.println("distance: " + distanceCurrentToMean);
        System.out.println("meanLat: " + meanPastLocationsLat + " | meanLon:" + meanPastLocationsLon);

        return predLocation;
    }

    // verify prediction with next location

}
