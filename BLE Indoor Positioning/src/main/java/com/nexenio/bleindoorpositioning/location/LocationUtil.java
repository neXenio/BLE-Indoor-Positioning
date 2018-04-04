package com.nexenio.bleindoorpositioning.location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 29.03.18.
 */

public class LocationUtil {

    public static Location meanLocationFromLast(List<Location> locationList, long amount, TimeUnit timeUnit) {
        List<Location> matchingLocations = getLocationsFromLast(locationList, amount, timeUnit);
        return calculateMeanLocation(matchingLocations);
    }

    public static Location calculateMeanLocation(List<Location> locationList) {
        if (locationList.size() < 1) {
            return null;
        } else {
            Location meanLocation = new Location();
            double latitudeSum = 0;
            double longitudeSum = 0;
            for (Location location : locationList) {
                latitudeSum += location.getLatitude();
                longitudeSum += location.getLongitude();
            }
            meanLocation.setLatitude(latitudeSum / locationList.size());
            meanLocation.setLongitude(longitudeSum / locationList.size());
            return meanLocation;
        }
    }

    public static List<Location> getLocationsBetween(List<Location> locationList, long minimumTimestamp, long maximumTimestamp) {
        List<Location> matchingLocations = new ArrayList<>();
        for (Location location : locationList) {
            if (location.getTimestamp() < minimumTimestamp || location.getTimestamp() >= maximumTimestamp) {
                continue;
            }
            matchingLocations.add(location);
        }
        return matchingLocations;
    }

    public static List<Location> getLocationsFromLast(List<Location> locationList, long amount, TimeUnit timeUnit) {
        return getLocationsBetween(locationList, System.currentTimeMillis() - timeUnit.toMillis(amount), System.currentTimeMillis());
    }

    public static List<Location> getLocationsSince(List<Location> locationList, long timestamp) {
        return getLocationsBetween(locationList, timestamp, System.currentTimeMillis());
    }

    public static List<Location> getLocationsBefore(List<Location> locationList, long timestamp) {
        return getLocationsBetween(locationList, 0, timestamp);
    }
}
