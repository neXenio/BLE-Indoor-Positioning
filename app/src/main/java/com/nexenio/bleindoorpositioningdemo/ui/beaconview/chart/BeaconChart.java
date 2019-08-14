package com.nexenio.bleindoorpositioningdemo.ui.beaconview.chart;

import android.content.Context;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconView;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.ColorUtil;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by steppschuh on 11.12.17.
 */

public abstract class BeaconChart extends BeaconView {

    @Retention(SOURCE)
    @IntDef({VALUE_TYPE_RSSI, VALUE_TYPE_RSSI_FILTERED, VALUE_TYPE_DISTANCE, VALUE_TYPE_FREQUENCY, VALUE_TYPE_VARIANCE})
    public @interface ValueType {
    }

    public static final int VALUE_TYPE_RSSI = 0;
    public static final int VALUE_TYPE_RSSI_FILTERED = 1;
    public static final int VALUE_TYPE_DISTANCE = 2;
    public static final int VALUE_TYPE_FREQUENCY = 3;
    public static final int VALUE_TYPE_VARIANCE = 4;

    @ValueType
    protected int valueType = VALUE_TYPE_RSSI_FILTERED;

    @ColorUtil.ColoringMode
    protected int coloringMode = ColorUtil.COLORING_MODE_INSTANCES;

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

    @Override
    public void initialize() {
        super.initialize();
        onValueTypeChanged();
        onColoringModeChanged();
    }

    protected void onValueTypeChanged() {
        invalidate();
    }

    protected void onColoringModeChanged() {
        invalidate();
    }

    /*
        Getter & Setter
     */

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
        onValueTypeChanged();
    }

    public int getColoringMode() {
        return coloringMode;
    }

    public void setColoringMode(int coloringMode) {
        this.coloringMode = coloringMode;
        onColoringModeChanged();
    }

}
