package app.incoder.lawrefbook.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * DisplayUtils
 *
 * @author : Jerry xu
 * @since : 2022/6/26 09:53
 */
public class DisplayUtils {

    /**
     * dp 转 px
     *
     * @param context context
     * @param dpVal DisplayMetrics 类中属性 density
     * @return px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px 转 dp
     *
     * @param context context
     * @param pxVal DisplayMetrics 类中属性 density
     * @return dp
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * 将 px 值转换为 dip 或 dp 值，保证尺寸大小不变
     *
     * @param context context
     * @param pxValue DisplayMetrics 类中属性 density
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将 dip 或 dp 值转换为 px 值，保证尺寸大小不变
     *
     * @param context  context
     * @param dipValue DisplayMetrics 类中属性 density
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将 px 值转换为 sp 值，保证文字大小不变
     *
     * @param context context
     * @param pxValue DisplayMetrics 类中属性 scaledDensity
     * @return sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将 sp 值转换为 px 值，保证文字大小不变
     *
     * @param context context
     * @param spValue DisplayMetrics 类中属性 scaledDensity
     * @return px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
