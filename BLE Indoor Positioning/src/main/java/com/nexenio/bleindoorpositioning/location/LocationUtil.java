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
        return meanLocation(matchingLocations);
    }

    public static Location meanLocation(List<Location> locationList) {
        if (locationList.size() > 1) {
            Location meanLocation = new Location();
            double latitudeSum = 0;
            double longitudeSum = 0;
            for (int i = 0; i < locationList.size(); i++) {
                latitudeSum += locationList.get(i).getLatitude();
                longitudeSum += locationList.get(i).getLongitude();
            }
            meanLocation.setLatitude(latitudeSum / locationList.size());
            meanLocation.setLongitude(longitudeSum / locationList.size());
            return meanLocation;
        } else if (locationList.size() == 1) {
            return locationList.get(0);
        } else {
            return null;
        }
    }

    public static List<Location> getLocationsBetween(List<Location> locationList, long startTimestamp, long stopTimestamp) {
        List<Location> matchingLocations = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getTimestamp() <= startTimestamp) {
                continue;
            }
            if (locationList.get(i).getTimestamp() > stopTimestamp) {
                continue;
            }
            matchingLocations.add(locationList.get(i));
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
