package app.incoder.lawrefbook.widget.mark;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import androidx.annotation.ColorInt;

/**
 * 标记
 *
 * @author Jerry Xu
 * @since 2025/6/6 23:16
 */
public class MarkUtils {

    /**
     * 涂抹高亮
     *
     * @param textView 文本
     * @param color    颜色
     */
    public static void highlightMark(TextView textView, @ColorInt int color) {
        // 获取当前 TextView 的文本内容
        CharSequence originalText = textView.getText();
        // 创建 SpannableStringBuilder，复制原始文本内容
        SpannableStringBuilder content = new SpannableStringBuilder(originalText);
        // 创建背景颜色 Span（红色）
        BackgroundColorSpan redBackgroundSpan = new BackgroundColorSpan(color);
        // 设置 Span：从 0 到长度的一半
        int spanStart = 0;
        int spanEnd = content.length() / 2;
        // 添加 Span
        content.setSpan(redBackgroundSpan, spanStart, spanEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // 设置回 TextView
        textView.setText(content);
    }

    /**
     * 下划线
     *
     * @param textView 文本
     */
    public static void applyUnderlineSpan(TextView textView) {
        // 获取当前 TextView 的文本内容
        CharSequence originalText = textView.getText();
        // 创建 SpannableStringBuilder，复制原始文本
        SpannableStringBuilder content = new SpannableStringBuilder(originalText);
        // 创建 UnderlineSpan 实例
        UnderlineSpan underlineSpan = new UnderlineSpan();
        // 设置 Span：从 0 到长度的一半
        int spanStart = 0;
        int spanEnd = content.length() / 2;
        // 添加 Span
        content.setSpan(underlineSpan, spanStart, spanEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // 设置回 TextView
        textView.setText(content);
    }
}
