package com.nexenio.bleindoorpositioning.location.provider;


import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.distance.LocationDistanceCalculator;

import java.util.ArrayList;
import java.util.List;

// import org.testng.annotations.Test;

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
        if (pastDeviceLocations.size() > locationWindow) {
            pastDeviceLocations.remove(0);
        }
    }

    public Location predictNewLocation(Location deviceCenter) {

        // TODO  what if no movement

        Location predLocation = new Location();

        double sumPastLocationsLat = 0;
        double sumPastLocationsLon = 0;

        for (int i = 0; i < pastDeviceLocations.size(); i++) {
            sumPastLocationsLat += pastDeviceLocations.get(i).getLatitude();
            sumPastLocationsLon += pastDeviceLocations.get(i).getLongitude();

        }
        double meanPastLocationsLat = sumPastLocationsLat / pastDeviceLocations.size();
        double meanPastLocationsLon = sumPastLocationsLon / pastDeviceLocations.size();

        // TODO calc speed of movement
        //double distanceToPrediction = LocationDistanceCalculator.calculateDistanceBetweeen(deviceCenter.getLatitude(),
        //        deviceCenter.getLongitude(), predLocation.getLatitude(), predLocation.getLongitude());

        double meanDistanceInMeters = LocationDistanceCalculator.calculateDistanceBetweeen(meanPastLocationsLat,
                meanPastLocationsLon, deviceCenter.getLatitude(), deviceCenter.getLongitude());


        // estimate bearing based on last known locations
        double sumBearing = 0;
        if(pastDeviceLocations.size() > 2){
            for (int i = pastDeviceLocations.size() - 1; i >= 1; i--) {
                sumBearing += calculateBearing(
                        pastDeviceLocations.get(i - 1).getLatitude(),
                        pastDeviceLocations.get(i - 1).getLongitude(),
                        pastDeviceLocations.get(i).getLatitude(),
                        pastDeviceLocations.get(i).getLongitude());
            }
        }


        double meanBearing = sumBearing / pastDeviceLocations.size();
        System.out.println("bearing: " + meanBearing);

        // get meanPastLocation
        // calc 360° angle of new position

        // average movement and direction of last points
        // add last movement pattern to current location

        //set window to start prediction
        if (pastDeviceLocations.size() == 0) {
            predLocation = deviceCenter;
        } else {
            //predLocation = pastDeviceLocations.get(pastDeviceLocations.size() - 1);
            predLocation = calculateNextLocation(deviceCenter.getLatitude(), deviceCenter.getLongitude(), meanDistanceInMeters / 1000, meanBearing);
            // predLocation.setLatitude(deviceCenter.getLatitude() + Math.random() * 0.00002);
            // predLocation.setLongitude(deviceCenter.getLongitude() + Math.random() * 0.00002);
        }

        //System.out.println("distance: " + meanDistanceInMeters);
        //System.out.println("meanLat: " + meanPastLocationsLat + " | meanLon:" + meanPastLocationsLon);

        return predLocation;
    }

    /**
     * @return the angle
     */

    protected static double calculateBearing(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        //TODO is this thing on? --> no values between ~270 and 360°
        fromLatitude = Math.toRadians(fromLatitude);
        toLatitude = Math.toRadians(toLatitude);
        double longitudeDifference = Math.toRadians(toLongitude - fromLongitude);
        double y = Math.sin(longitudeDifference) * Math.cos(toLatitude);
        double x = (Math.cos(fromLatitude) * Math.sin(toLatitude)) - (Math.sin(fromLatitude) * Math.cos(toLatitude) * Math.cos(longitudeDifference));

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    /**
     * @param distance in km
     * @param bearing  in degrees
     * @return Location
     */
    protected static Location calculateNextLocation(double fromLatitude, double fromLongitude, double distance, double bearing) {
        double bearingRadians = Math.toRadians(bearing);
        double latitudeRadians = Math.toRadians(fromLatitude);
        double longitudeRadians = Math.toRadians(fromLongitude);

        double newLatitude = Math.asin(Math.sin(latitudeRadians) * Math.cos(distance / LocationDistanceCalculator.EARTH_RADIUS) +
                Math.cos(latitudeRadians) * Math.sin(distance / LocationDistanceCalculator.EARTH_RADIUS) * Math.cos(bearingRadians));

        double newLongitude = longitudeRadians + Math.atan2(Math.sin(bearingRadians) * Math.sin(distance / LocationDistanceCalculator.EARTH_RADIUS) *
                Math.cos(latitudeRadians), Math.cos(distance / LocationDistanceCalculator.EARTH_RADIUS) - Math.sin(latitudeRadians) * Math.sin(newLatitude));

        return new Location(Math.toDegrees(newLatitude), Math.toDegrees(newLongitude));
    }


    // verify prediction with next location

}
