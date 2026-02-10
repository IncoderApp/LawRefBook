package app.incoder.lawrefbook.widget.mark;

import android.os.Build;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;

import androidx.annotation.RequiresApi;

/**
 * CustomUnderLine
 *
 * @author Jerry Xu
 * @since 2025/6/6 23:17
 */
public class CustomUnderLine extends UnderlineSpan {

    private final int color;
    private final float underlineThickness;

    public CustomUnderLine(int color, float underlineThickness) {
        this.color = color;
        this.underlineThickness = underlineThickness;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(true); // 保持下划线启用
        ds.underlineColor = color; // 设置颜色（API 29+）
        ds.underlineThickness = underlineThickness; // 设置粗细（API 29+）
        super.updateDrawState(ds);
    }
}