package app.incoder.lawrefbook.widget.mark;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * 绘制波浪线
 *
 * @author Jerry Xu
 * @since 2025/6/6 23:10
 */
public class WaveLineBackgroundSpan implements LineBackgroundSpan {

    private final int waveColor;
    private final Paint wavePaint;

    public WaveLineBackgroundSpan(@ColorInt int waveColor) {
        this.waveColor = waveColor;
        wavePaint = new Paint();
        wavePaint.setColor(waveColor);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setStrokeWidth(6f);
    }

    /**
     * 绘制背景的方法，在画布上绘制指定颜色的矩形作为行背景
     *
     * @param canvas     canvas on which the span should be rendered
     * @param paint      paint used to draw text, which should be left unchanged on exit
     * @param left       left position of the line relative to input canvas, in pixels
     * @param right      right position of the line relative to input canvas, in pixels
     * @param top        top position of the line relative to input canvas, in pixels
     * @param baseline   baseline of the text relative to input canvas, in pixels
     * @param bottom     bottom position of the line relative to input canvas, in pixels
     * @param text       current text
     * @param start      start character index of the line
     * @param end        end character index of the line
     * @param lineNumber line number in the current text layout
     */
    @Override
    public void drawBackground(@NonNull Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lineNumber) {

        final int amplitude = 5;
        final int wavelength = 15;

        // 提取当前文本段落内容
        String subText = text.subSequence(start, end).toString();

        // 计算文本宽度
        float width = paint.measureText(subText);

        // 绘制波浪线
        for (int x = left; x < left + width && x <= right; x++) {
            double radians = (double) x / wavelength;
            float y = (float) (amplitude * Math.sin(radians));
            canvas.drawPoint(x, bottom + y, wavePaint);
        }
    }
}