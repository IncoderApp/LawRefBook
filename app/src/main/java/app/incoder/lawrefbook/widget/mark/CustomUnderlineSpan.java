package app.incoder.lawrefbook.widget.mark;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

/**
 * 自定义下划线
 *
 * @author Jerry Xu
 * @since 2025/6/6 23:19
 */
public class CustomUnderlineSpan extends ReplacementSpan {

    private final int underlineColor;
    // 下划线高度（粗细）
    private final float underlineHeight;

    public CustomUnderlineSpan(int underlineColor, float underlineHeight) {
        this.underlineColor = underlineColor;
        this.underlineHeight = underlineHeight;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        // 返回文本宽度，用于测量
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {

        // 绘制原始文本
        canvas.drawText(text, start, end, x, y, paint);

        // 创建一个独立的画笔用于绘制下划线
        Paint underlinePaint = new Paint(paint);
        underlinePaint.setStyle(Paint.Style.FILL);
        underlinePaint.setColor(underlineColor);
        underlinePaint.setStrokeWidth(underlineHeight);

        // 计算下划线位置
        float textWidth = paint.measureText(text, start, end);
        float underlineTop = bottom + underlineHeight / 2;

        // 绘制一条直线作为下划线
        canvas.drawLine(x, underlineTop, x + textWidth, underlineTop, underlinePaint);
    }
}