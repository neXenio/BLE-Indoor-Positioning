package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconView;

/**
 * Created by steppschuh on 11.12.17.
 */

public abstract class BeaconChart extends BeaconView {

    public BeaconChart(Context context) {
        super(context);
    }

    public BeaconChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BeaconChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BeaconChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
