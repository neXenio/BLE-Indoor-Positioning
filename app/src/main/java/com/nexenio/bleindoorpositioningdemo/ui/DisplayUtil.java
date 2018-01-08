package com.nexenio.bleindoorpositioningdemo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @see <a href="https://stackoverflow.com/a/9563438/1188330">StackOverflow</a>
 */

public abstract class DisplayUtil {

    public static float convertDipToPixels(float dip) {
        return convertDipToPixels(dip, Resources.getSystem().getDisplayMetrics());
    }

    public static float convertDipToPixels(float dip, Context context) {
        return convertDipToPixels(dip, context.getResources().getDisplayMetrics());
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dip A value in dp (density independent pixels) unit. Which we need to convert into
     *            pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDipToPixels(float dip, DisplayMetrics displayMetrics) {
        return dip * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param pixels  A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDip(float pixels, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return pixels / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
