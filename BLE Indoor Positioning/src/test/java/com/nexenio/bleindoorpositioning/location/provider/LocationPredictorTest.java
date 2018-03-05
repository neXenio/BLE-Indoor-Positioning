package com.nexenio.bleindoorpositioning.location.provider;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationPredictor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 14.02.18.
 */
public class LocationPredictorTest {

    public static final double HUMAN_WALKING_SPEED = 1.388889; // meters per second
    public static final Location GENDARMENMARKT = new Location(52.513588, 13.392995);

    @Test
    public void predictLocationFromLocations_locationsInLine_accuratePrediction() throws Exception {
        List<Location> lineOnGendarmenmarkt = createWalkingLine(GENDARMENMARKT);
        Location lastKnownLocation = lineOnGendarmenmarkt.get(lineOnGendarmenmarkt.size() - 2);
        Location expectedLocation = lineOnGendarmenmarkt.get(lineOnGendarmenmarkt.size() - 1);
        List<Location> subList = lineOnGendarmenmarkt.subList(0, lineOnGendarmenmarkt.size() - 2);
        Location predictedLocation = LocationPredictor.predict(subList, TimeUnit.SECONDS.toMillis(1));
        double expectedDistance = lastKnownLocation.getDistanceTo(expectedLocation);
        double actualDistance = lastKnownLocation.getDistanceTo(predictedLocation);
        assertEquals(expectedDistance, actualDistance, 0.1);
    }

    @Test
    public void predictLocationFromLocations_locationsInCircle_accuratePrediction() throws Exception {
        List<Location> walkInACircle = createWalkingCircle(GENDARMENMARKT);

        for (int i = 0; i < walkInACircle.size() - 3; i++) {
            List<Location> subList = walkInACircle.subList(i, i + 3);
            Location lastKnownLocation = walkInACircle.get(i + 2);

            Location expectedLocation = walkInACircle.get(i + 3);
            Location predictedLocation = LocationPredictor.predict(subList, TimeUnit.SECONDS.toMillis(1));

            // angle
            double expectedAngle = lastKnownLocation.getAngleTo(expectedLocation);
            double actualAngle = lastKnownLocation.getAngleTo(predictedLocation);
            double angleDistance = angleDistance(expectedAngle, actualAngle);
            assertEquals(0, angleDistance, 50);

            // distance
            double expectedDistance = lastKnownLocation.getDistanceTo(expectedLocation);
            double actualDistance = lastKnownLocation.getDistanceTo(predictedLocation);
            assertEquals(expectedDistance, actualDistance, 1);
        }
    }

    @Test
    public void calculateSpeed_locationsLine_correctSpeed() throws Exception {
        List<Location> lineOnGendarmenmarkt = createWalkingLine(GENDARMENMARKT);
        double speed = LocationPredictor.calculateSpeed(lineOnGendarmenmarkt);
        assertEquals(speed, HUMAN_WALKING_SPEED, 0.1);
    }

    @Test
    public void calculateSpeed_locationCircle_correctSpeed() throws Exception {
        List<Location> walkInACircle = createWalkingCircle(GENDARMENMARKT);
        for (int i = 0; i < walkInACircle.size() - 3; i++) {
            List<Location> subList = walkInACircle.subList(i, i + 3);
            double speed = LocationPredictor.calculateSpeed(subList);
            assertEquals(speed, HUMAN_WALKING_SPEED, 0.1);
        }
    }

    @Test
    public void angleDistance_angles_correctDistance() throws Exception {
        double angleDistance = angleDistance(10, 350);
        assertEquals(20, angleDistance, 0);

        angleDistance = angleDistance(170, 190);
        assertEquals(20, angleDistance, 0);
    }

    public List<Location> createWalkingLine(Location location) {
        return createWalkingLocations(location, 0, 0, 20);
    }

    public List<Location> createWalkingCircle(Location location) {
        return createWalkingLocations(location, 0, 360, 36);
    }

    public static List<Location> createWalkingLocations(Location startLocation, double startAngle, double stopAngle, int count) {
        long timestampDelta = 500;
        double distanceToNextLocation = HUMAN_WALKING_SPEED * ((float) timestampDelta / TimeUnit.SECONDS.toMillis(1));
        List<Location> locations = new ArrayList<>();
        locations.add(startLocation);

        if (count <= 1) {
            return locations;
        }

        double angleFraction = Math.abs(startAngle - stopAngle) / (count - 1);
        for (int i = 0; i <= count; i++) {
            double angle = (startAngle + angleFraction * i);
            startLocation = startLocation.getShiftedLocation(distanceToNextLocation, angle);
            startLocation.setTimestamp(locations.get(locations.size() - 1).getTimestamp() + timestampDelta);
            locations.add(startLocation);
        }
        return locations;
    }

    /**
     * @see <a href="https://stackoverflow.com/questions/7570808/how-do-i-calculate-the-difference-of-two-angle-measures">Stackoverflow</a>
     * Length (angular) of a shortest way between two angles. It will be in range [0, 180].
     */
    private double angleDistance(double alpha, double beta) {
        double phi = Math.abs(beta - alpha) % 360;       // This is either the distance or 360 - distance
        return phi > 180 ? 360 - phi : phi;
    }
}