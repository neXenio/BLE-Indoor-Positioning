package com.nexenio.bleindoorpositioningdemo.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import androidx.annotation.NonNull;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;

/**
 * Created by steppschuh on 22.11.17.
 */

public class LocationAnimator implements LocationProvider {

    public static final long ANIMATION_DURATION_SHORT = 200;
    public static final long ANIMATION_DURATION_MEDIUM = 400;
    public static final long ANIMATION_DURATION_LONG = 500;

    private Location originLocation;
    private Location currentLocation;
    private Location targetLocation;

    private ValueAnimator latitudeAnimator;
    private ValueAnimator longitudeAnimator;
    private ValueAnimator accuracyAnimator;
    private Animator.AnimatorListener animatorListener;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private long animationDuration = ANIMATION_DURATION_LONG;

    private double latitudeDelta;
    private double longitudeDelta;
    private double accuracyDelta;

    private LocationListener locationListener;

    public LocationAnimator(@NonNull Location originLocation, @NonNull Location targetLocation) {
        this.originLocation = originLocation;
        this.targetLocation = targetLocation;
        this.currentLocation = new Location(originLocation);
        latitudeDelta = targetLocation.getLatitude() - originLocation.getLatitude();
        longitudeDelta = targetLocation.getLongitude() - originLocation.getLongitude();
        accuracyDelta = targetLocation.getAccuracy() - originLocation.getAccuracy();
        setupAnimators();
    }

    public LocationAnimator(@NonNull Location originLocation, @NonNull Location targetLocation, long animationDuration) {
        this(originLocation, targetLocation);
        this.animationDuration = animationDuration;
    }

    private void setupAnimators() {
        animatorListener = createAnimatorListener();
        animatorUpdateListener = createAnimatorUpdateListener();
        latitudeAnimator = createAnimator();
        longitudeAnimator = createAnimator();
        accuracyAnimator = createAnimator();
    }

    private ValueAnimator createAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(animationDuration);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.addListener(animatorListener);
        return valueAnimator;
    }

    private Animator.AnimatorListener createAnimatorListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                currentLocation.setLatitude(targetLocation.getLatitude());
                currentLocation.setLongitude(targetLocation.getLongitude());
                onCurrentLocationUpdated();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
    }

    private ValueAnimator.AnimatorUpdateListener createAnimatorUpdateListener() {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                if (valueAnimator == latitudeAnimator) {
                    currentLocation.setLatitude(originLocation.getLatitude() + (latitudeDelta * value));
                } else if (valueAnimator == longitudeAnimator) {
                    currentLocation.setLongitude(originLocation.getLongitude() + (longitudeDelta * value));
                } else if (valueAnimator == accuracyAnimator) {
                    currentLocation.setAccuracy(originLocation.getAccuracy() + (accuracyDelta * value));
                }
                onCurrentLocationUpdated();
            }
        };
    }

    public void start() {
        latitudeAnimator.start();
        longitudeAnimator.start();
        accuracyAnimator.start();
    }

    public void cancel() {
        latitudeAnimator.cancel();
        longitudeAnimator.cancel();
        accuracyAnimator.cancel();
    }

    private void onCurrentLocationUpdated() {
        if (locationListener != null) {
            locationListener.onLocationUpdated(this, currentLocation);
        }
    }

    @Override
    public Location getLocation() {
        return currentLocation;
    }

    /*
        Getter & Setter
     */

    public Location getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(Location originLocation) {
        this.originLocation = originLocation;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public ValueAnimator getLatitudeAnimator() {
        return latitudeAnimator;
    }

    public void setLatitudeAnimator(ValueAnimator latitudeAnimator) {
        this.latitudeAnimator = latitudeAnimator;
    }

    public ValueAnimator getLongitudeAnimator() {
        return longitudeAnimator;
    }

    public void setLongitudeAnimator(ValueAnimator longitudeAnimator) {
        this.longitudeAnimator = longitudeAnimator;
    }

    public ValueAnimator getAccuracyAnimator() {
        return accuracyAnimator;
    }

    public void setAccuracyAnimator(ValueAnimator accuracyAnimator) {
        this.accuracyAnimator = accuracyAnimator;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

}
