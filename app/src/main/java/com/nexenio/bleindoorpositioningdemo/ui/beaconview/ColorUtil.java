package com.nexenio.bleindoorpositioningdemo.ui.beaconview;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.nexenio.bleindoorpositioningdemo.R;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by steppschuh on 12.12.17.
 */

public class ColorUtil {

    @Retention(SOURCE)
    @IntDef({COLORING_MODE_INSTANCES, COLORING_MODE_TYPES, COLORING_MODE_PROPERTIES})
    public @interface ColoringMode {
    }

    public static final int COLORING_MODE_INSTANCES = 0;
    public static final int COLORING_MODE_TYPES = 1;
    public static final int COLORING_MODE_PROPERTIES = 2;

    public static int[] MATERIAL_DESIGN_COLOR_RESOURCE_IDS = {
            R.color.md_red_500, R.color.md_pink_500, R.color.md_deep_purple_500, R.color.md_deep_purple_500,
            R.color.md_indigo_500, R.color.md_blue_500, R.color.md_light_blue_500, R.color.md_cyan_500, R.color.md_teal_500,
            R.color.md_green_500, R.color.md_light_green_500, R.color.md_lime_500, R.color.md_yellow_500, R.color.md_amber_500,
            R.color.md_orange_500, R.color.md_deep_orange_500, R.color.md_brown_500, R.color.md_blue_grey_500, R.color.md_blue_grey_500
    };

    public static int[] MATERIAL_DESIGN_LIGHT_COLOR_RESOURCE_IDS = {
            R.color.md_red_200, R.color.md_pink_200, R.color.md_deep_purple_200, R.color.md_deep_purple_200,
            R.color.md_indigo_200, R.color.md_blue_200, R.color.md_light_blue_200, R.color.md_cyan_200, R.color.md_teal_200,
            R.color.md_green_200, R.color.md_light_green_200, R.color.md_lime_200, R.color.md_yellow_200, R.color.md_amber_200,
            R.color.md_orange_200, R.color.md_deep_orange_200, R.color.md_brown_200, R.color.md_blue_grey_200, R.color.md_blue_grey_200
    };

    public static int[] MATERIAL_DESIGN_DARK_COLOR_RESOURCE_IDS = {
            R.color.md_red_800, R.color.md_pink_800, R.color.md_deep_purple_800, R.color.md_deep_purple_800,
            R.color.md_indigo_800, R.color.md_blue_800, R.color.md_light_blue_800, R.color.md_cyan_800, R.color.md_teal_800,
            R.color.md_green_800, R.color.md_light_green_800, R.color.md_lime_800, R.color.md_yellow_800, R.color.md_amber_800,
            R.color.md_orange_800, R.color.md_deep_orange_800, R.color.md_brown_800, R.color.md_blue_grey_800, R.color.md_blue_grey_800
    };

    private int[] materialDesignColors;
    private int[] materialDesignLightColors;
    private int[] materialDesignDarkColors;

    private static ColorUtil instance;

    private ColorUtil() {

    }

    public static ColorUtil getInstance() {
        if (instance == null) {
            instance = new ColorUtil();
        }
        return instance;
    }

    public static void initialize(@NonNull Context context) {
        ColorUtil instance = getInstance();
        instance.materialDesignColors = getColors(context, MATERIAL_DESIGN_COLOR_RESOURCE_IDS);
        instance.materialDesignLightColors = getColors(context, MATERIAL_DESIGN_LIGHT_COLOR_RESOURCE_IDS);
        instance.materialDesignDarkColors = getColors(context, MATERIAL_DESIGN_DARK_COLOR_RESOURCE_IDS);
    }

    private static int[] getColors(@NonNull Context context, int[] colorResourceIds) {
        Resources resources = context.getResources();
        int[] colors = new int[colorResourceIds.length];
        for (int colorIndex = 0; colorIndex < colorResourceIds.length; colorIndex++) {
            colors[colorIndex] = ResourcesCompat.getColor(resources, colorResourceIds[colorIndex], null);
        }
        return colors;
    }

    @ColorInt
    public static int getBeaconColor(int beaconIndex) {
        int colorIndex = 18 + (beaconIndex * 2);
        colorIndex = colorIndex % MATERIAL_DESIGN_COLOR_RESOURCE_IDS.length;
        return getInstance().materialDesignDarkColors[colorIndex];
    }

}
