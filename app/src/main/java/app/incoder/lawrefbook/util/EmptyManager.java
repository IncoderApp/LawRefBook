package app.incoder.lawrefbook.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.incoder.lawrefbook.R;

/**
 * EmptyManager
 *
 * @author : Jerry xu
 * @since : 2022/5/15 15:47
 */
public class EmptyManager {

    public static void setEmptyResource(View mEmpty, int resId, String text) {
        ImageView ivEmpty = mEmpty.findViewById(R.id.iv_empty);
        TextView tvEmpty = mEmpty.findViewById(R.id.tv_empty);

        if (resId != 0) {
            ivEmpty.setVisibility(View.VISIBLE);
            ivEmpty.setImageResource(resId);
        } else {
            ivEmpty.setVisibility(View.GONE);
        }
        if (null == text || "".equals(text)) {
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(text);
        }
    }

}
