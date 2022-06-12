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

package app.incoder.lawrefbook.ui.feed;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

import app.incoder.lawrefbook.storage.Law;

/**
 * FeedDiffCallBack
 *
 * @author : Jerry xu
 * @since : 2022/5/19 00:42
 */
public class FeedDiffCallBack extends DiffUtil.Callback {

    private final List<Law> mNewLaws;
    private final List<Law> mOldLaws;

    public FeedDiffCallBack(List<Law> newLaws, List<Law> oldLaws) {
        this.mNewLaws = newLaws;
        this.mOldLaws = oldLaws;
    }

    @Override
    public int getOldListSize() {
        return mOldLaws != null ? mOldLaws.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewLaws != null ? mNewLaws.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldLaws.get(oldItemPosition).getName().equals(mNewLaws.get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Law beanOld = mOldLaws.get(oldItemPosition);
        Law beanNew = mNewLaws.get(newItemPosition);
        if (!beanOld.getName().equals(beanNew.getName())) {
            // 如果有内容不同，就返回 false
            return false;
        }
        // 如果有内容不同，就返回 false
        return !Objects.equals(beanOld.getId(), beanNew.getId());
    }
}
