package app.incoder.lawrefbook.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.EditText;

import app.incoder.lawrefbook.R;

/**
 * Image
 *
 * <a href="https://blog.csdn.net/xuqiqiang1993/article/details/67636165">Android StaticLayout实现主流便签内容生成长图功能</a>
 *
 * @author : Jerry xu
 * @since : 2022/6/26 09:52
 */
public class Image {

    public static Bitmap getBitmap(Activity context, EditText mEditText) {
        Editable editable = mEditText.getText();
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setARGB(0xFF, 0, 0, 0);
        textPaint.setTextSize(mEditText.getTextSize() - DisplayUtils.sp2px(context, 2));

        ScreenInfo mScreenInfo = ScreenInfo.getInstance(context);

        float bitmapWidth = mScreenInfo.getWidthPixels() - DisplayUtils.dip2px(context, 10);
        float padding = DisplayUtils.dip2px(context, 15);

        StaticLayout layout = StaticLayout.Builder.obtain(editable, 0, editable.length(), textPaint, (int) (bitmapWidth - padding * 4))
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0.0F, 1.2F)
                .setIncludePad(true)
                .build();
        float bitmapHeight = layout.getHeight() + padding * 10;

        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap((int) bitmapWidth, (int) bitmapHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
        // background
        Canvas paper = new Canvas(bitmap);
        paper.drawColor(Color.WHITE);
        paper.translate(padding * 2, padding * 4);
        layout.draw(paper);
        paper.translate(-padding * 2, -padding * 4);

        Paint mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(DisplayUtils.dip2px(context, 0.5f));

        float offset = DisplayUtils.dip2px(context, 2);

        paper.drawRect(new Rect((int) (padding),
                (int) (padding * 2),
                (int) (bitmapWidth - padding),
                (int) (bitmapHeight - padding * 4)), mPaint);

        paper.drawRect(new Rect((int) (padding - offset),
                (int) (padding * 2 - offset),
                (int) (padding),
                (int) (padding * 2)), mPaint);

        paper.drawRect(new Rect((int) (bitmapWidth - padding),
                (int) (padding * 2 - offset),
                (int) (bitmapWidth - padding + offset),
                (int) (padding * 2)), mPaint);

        paper.drawRect(new Rect((int) (padding - offset),
                (int) (bitmapHeight - padding * 4),
                (int) (padding),
                (int) (bitmapHeight - padding * 4 + offset)), mPaint);

        paper.drawRect(new Rect((int) (bitmapWidth - padding),
                (int) (bitmapHeight - padding * 4),
                (int) (bitmapWidth - padding + offset),
                (int) (bitmapHeight - padding * 4 + offset)), mPaint);

        paper.drawRect(new Rect((int) (padding + offset),
                (int) (padding * 2 + offset),
                (int) (bitmapWidth - padding - offset),
                (int) (bitmapHeight - padding * 4 - offset)), mPaint);

        TextPaint fromTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        fromTextPaint.setColor(Color.GRAY);
        fromTextPaint.setTextSize(DisplayUtils.sp2px(context, 12));

        // TAIL
        String appName = context.getResources().getString(R.string.app_name);
        String source = "来至《" + appName + "》";
        StaticLayout fromLayout = StaticLayout.Builder.obtain(source, 0, source.length(), fromTextPaint, (int) (bitmapWidth - padding * 3.5))
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0.0F, 1.0F)
                .setIncludePad(true)
                .build();

        paper.translate(padding, bitmapHeight - padding * 3);
        fromLayout.draw(paper);

        return bitmap;
    }

}
