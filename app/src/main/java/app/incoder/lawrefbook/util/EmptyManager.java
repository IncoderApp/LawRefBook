/*
 * Copyright (C) 2022 The Jerry xu Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
