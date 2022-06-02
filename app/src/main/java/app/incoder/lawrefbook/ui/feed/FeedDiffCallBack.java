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

import java.util.Objects;

import app.incoder.lawrefbook.model.Lawre;

/**
 * FeedDiffCallBack
 *
 * @author : Jerry xu
 * @since : 2022/5/19 00:42
 */
public class FeedDiffCallBack extends DiffUtil.Callback {

    private final Lawre mNewLaws;
    private final Lawre mOldLaws;

    public FeedDiffCallBack(Lawre newLaws, Lawre oldLaws) {
        this.mNewLaws = newLaws;
        this.mOldLaws = oldLaws;
    }

    @Override
    public int getOldListSize() {
        return mOldLaws.getLaws() != null ? mOldLaws.getLaws().size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewLaws.getLaws() != null ? mNewLaws.getLaws().size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldLaws.getLaws().get(oldItemPosition).getName().equals(mNewLaws.getLaws().get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Lawre.LawsBean beanOld = mOldLaws.getLaws().get(oldItemPosition);
        Lawre.LawsBean beanNew = mNewLaws.getLaws().get(newItemPosition);
        if (!beanOld.getName().equals(beanNew.getName())) {
            // 如果有内容不同，就返回 false
            return false;
        }
        // 如果有内容不同，就返回 false
        return !Objects.equals(beanOld.getId(), beanNew.getId());
    }
}
