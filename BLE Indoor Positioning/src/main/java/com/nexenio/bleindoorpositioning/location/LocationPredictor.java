package com.nexenio.bleindoorpositioning.location;

import com.nexenio.bleindoorpositioning.location.angle.AngleUtil;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Stores window of last locations and tries to predict the next location based on average distance
 * and angle during the window
 *
 * Created by leon on 29.01.18.
 */

public class LocationPredictor implements LocationProvider {

    /**
     * Locations that the prediction will be based on.
     *
     * Note: must be sorted by {@link Location#timestamp} (ascending, oldest location first).
     */
    private List<Location> recentLocations = new ArrayList<>();

    private Location predictedLocation;

    private boolean shouldUpdatePrediction = true;

    /**
     * The amount of milliseconds that the predictor will 'look into the future'. A value of 2000
     * will result in predictions of the location in 2 seconds from now.
     */
    private long predictionDuration = TimeUnit.SECONDS.toMillis(1);

    /**
     * The amount of milliseconds that the predictor will 'look into the past'. A value of 2000 will
     * result in predictions based on locations from the past 2 seconds.
     */
    private long recentLocationDuration = TimeUnit.SECONDS.toMillis(3);

    public LocationPredictor() {
    }

    @Override
    public Location getLocation() {
        if (shouldUpdatePrediction) {
            predict();
        }
        return predictedLocation;
    }

    public void invalidatePrediction() {
        shouldUpdatePrediction = true;
    }

    public void predict() {
        predictedLocation = predict(new ArrayList<>(recentLocations), predictionDuration);
        shouldUpdatePrediction = false;
    }

    public void addLocation(Location location) {
        recentLocations.add(location);
        removeOldLocations();
        invalidatePrediction();
    }

    private void removeOldLocations() {
        List<Location> oldLocations = new ArrayList<>();
        long minimumTimestamp = System.currentTimeMillis() - recentLocationDuration;
        Location location;
        for (int i = 0; i < recentLocations.size(); i++) {
            location = recentLocations.get(i);
            if (location.getTimestamp() < minimumTimestamp) {
                oldLocations.add(location);
            } else {
                break;
            }
        }
        recentLocations.removeAll(oldLocations);
    }

    /**
     * Will predict the location after the specified predictionDuration by looking at the specified
     * recent locations.
     *
     * @param locations the recent locations
     * @param duration  amount of milliseconds to look into the future
     */
    public static Location predict(List<Location> locations, long duration) {
        if (locations.isEmpty()) {
            return null;
        }

        Location lastLocation = locations.get(locations.size() - 1);
        if (locations.size() == 1) {
            Location predictedLocation = new Location(lastLocation);
            predictedLocation.setTimestamp(lastLocation.getTimestamp() + duration);
            return predictedLocation;
        }

        double angle = AngleUtil.calculateMeanAngle(locations);
        double speed = calculateSpeed(locations);
        double distance = speed * TimeUnit.MILLISECONDS.toSeconds(duration);

        Location predictedLocation = new Location(lastLocation);
        predictedLocation.setTimestamp(lastLocation.getTimestamp() + duration);
        return predictedLocation.getShiftedLocation(distance, angle);
    }

    /**
     * Calculates the speed in meters per second.
     */
    public static double calculateSpeed(List<Location> locations) {
        if (locations.size() < 2) {
            return 0;
        }
        Location firstLocation = locations.get(0);
        Location lastLocation = locations.get(locations.size() - 1);
        return calculateSpeed(firstLocation, lastLocation);
    }

    /**
     * Calculates the speed in meters per second.
     */
    public static double calculateSpeed(Location originLocation, Location currentLocation) {
        if (!originLocation.hasLatitudeAndLongitude() || !currentLocation.hasLatitudeAndLongitude()) {
            return 0;
        }
        long duration = Math.abs(currentLocation.getTimestamp() - originLocation.getTimestamp());
        double distance = originLocation.getDistanceTo(currentLocation);
        return duration > 0 ? distance / (TimeUnit.MILLISECONDS.toSeconds(duration)) : 0;
    }

    /*
        Getter & Setter
     */

    public List<Location> getRecentLocations() {
        return recentLocations;
    }

    public void setRecentLocations(List<Location> recentLocations) {
        this.recentLocations = recentLocations;
        invalidatePrediction();
    }

    public long getPredictionDuration() {
        return predictionDuration;
    }

    public void setPredictionDuration(long predictionDuration) {
        this.predictionDuration = predictionDuration;
        invalidatePrediction();
    }
}
