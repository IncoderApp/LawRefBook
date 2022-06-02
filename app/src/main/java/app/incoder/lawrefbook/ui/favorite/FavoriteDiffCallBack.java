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

package app.incoder.lawrefbook.ui.favorite;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import app.incoder.lawrefbook.storage.Libraries;

/**
 * ContentDiffCallBack
 *
 * @author : Jerry xu
 * @since : 2022/5/19 23:31
 */
public class FavoriteDiffCallBack extends DiffUtil.Callback {

    private final List<Libraries> mNewContent;
    private final List<Libraries> mOldContent;

    public FavoriteDiffCallBack(List<Libraries> newContent, List<Libraries> oldContent) {
        this.mNewContent = newContent;
        this.mOldContent = oldContent;
    }

    @Override
    public int getOldListSize() {
        return mOldContent != null ? mOldContent.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewContent != null ? mNewContent.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return !mOldContent.get(oldItemPosition).equals(mNewContent.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Libraries oldContent = mOldContent.get(oldItemPosition);
        Libraries newContent = mNewContent.get(newItemPosition);
        return oldContent.getId() != newContent.getId();
    }
}
