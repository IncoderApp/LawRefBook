package app.incoder.lawrefbook.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * ScreenInfo
 *
 * @author : Jerry xu
 * @since : 2022/6/26 10:03
 */
public class ScreenInfo {

    private final int widthPixels;
    private final int heightPixels;

    private static ScreenInfo instance;

    private ScreenInfo(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();

        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.widthPixels = dm.widthPixels;
        this.heightPixels = dm.heightPixels;
    }

    public static ScreenInfo getInstance() {
        return instance;
    }

    public static ScreenInfo getInstance(Activity context) {
        if (instance == null) {
            instance = new ScreenInfo(context);
        }
        return instance;
    }

    /**
     * @return the number of pixel in the width of the screen.
     */
    public int getWidthPixels() {
        return widthPixels;
    }

    /**
     * @return the number of pixel in the height of the screen.
     */
    public int getHeightPixels() {
        return heightPixels;
    }

    public String getSize() {
        return widthPixels + "×" + heightPixels;
    }
}