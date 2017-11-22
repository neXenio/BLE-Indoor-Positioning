package com.nexenio.bleindoorpositioningdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.listener.LocationListener;
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
    private Animator.AnimatorListener animatorListener;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private long animationDuration = ANIMATION_DURATION_LONG;

    private LocationListener locationListener;

    public LocationAnimator(@NonNull Location originLocation, @NonNull Location targetLocation) {
        this.originLocation = originLocation;
        this.targetLocation = targetLocation;
        this.currentLocation = new Location(originLocation);
        setupAnimators();
    }

    public LocationAnimator(@NonNull Location originLocation, @NonNull Location targetLocation, long animationDuration) {
        this(originLocation, targetLocation);
        this.animationDuration = animationDuration;
    }

    private void setupAnimators() {
        animatorListener = createAnimatorListener();
        animatorUpdateListener = createAnimatorUpdateListener();
        latitudeAnimator = createAnimator((float) originLocation.getLatitude(), (float) targetLocation.getLatitude());
        longitudeAnimator = createAnimator((float) originLocation.getLongitude(), (float) targetLocation.getLongitude());
    }

    private ValueAnimator createAnimator(float fromValue, float toValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromValue, toValue);
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
                    currentLocation.setLatitude(value);
                } else if (valueAnimator == longitudeAnimator) {
                    currentLocation.setLongitude(value);
                }
                onCurrentLocationUpdated();
            }
        };
    }

    public void start() {
        latitudeAnimator.start();
        longitudeAnimator.start();
    }

    public void cancel() {
        latitudeAnimator.cancel();
        longitudeAnimator.cancel();
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

}
