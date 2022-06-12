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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.storage.Libraries;
import app.incoder.lawrefbook.ui.content.ContentActivity;

/**
 * FavoriteAdapter
 *
 * @author : Jerry xu
 * @since : 2022/5/2 17:17
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<Libraries> mLibraries;
    private SelectionTracker<Long> selectionTracker;
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;

    public FavoriteAdapter() {

    }

    void setLibraries(List<Libraries> libraries) {
        this.mLibraries = libraries;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = inflater.inflate(R.layout.empty_view, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_favorite, parent, false);
        }
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        if (mLibraries != null && mLibraries.size() > 0) {
            Libraries item = mLibraries.get(position);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), ContentActivity.class)
                        .putExtra(ContentActivity.mPath, item.getArticlePath())
                        .putExtra(ContentActivity.mArticleId, item.getLawsId())
                        .putExtra(ContentActivity.mTitle, item.getName());
                holder.itemView.getContext().startActivity(intent);
            });
            holder.bind(item, holder.itemView.getContext(), position);
        }
    }

    @Override
    public int getItemCount() {
        if (mLibraries != null && mLibraries.size() > 0) {
            return mLibraries.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mLibraries == null || mLibraries.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_ITEM;
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView materialCardView;
        private final Details details;
        private final TextView mTitle;
        private final TextView mContent;
        private final TextView mFolder;
        private final TextView mType;
        private final TextView mDate;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.mcv_favorite);
            mTitle = itemView.findViewById(R.id.tv_favorite_title);
            mContent = itemView.findViewById(R.id.tv_favorite_content);
            mFolder = itemView.findViewById(R.id.tv_folder);
            mType = itemView.findViewById(R.id.tv_type);
            mDate = itemView.findViewById(R.id.tv_date);
            details = new Details();
        }

        private void bind(Libraries libraries, Context context, int position) {
            details.position = position;
            mTitle.setText(libraries.getName());
            mContent.setText(libraries.getSnippetsContent());
            mFolder.setText(String.format(context.getResources().getString(R.string.article_folder), libraries.getArticleFolder()));
            mType.setText("full".equals(libraries.getClassify()) ? "全文" : "片段");
            mDate.setText(libraries.getCreateTime());
            if (selectionTracker != null) {
                bindSelectedState();
            }
        }

        private void bindSelectedState() {
            materialCardView.setChecked(selectionTracker.isSelected(details.getSelectionKey()));
        }

        ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return details;
        }
    }

    static class DetailsLookup extends ItemDetailsLookup<Long> {

        private final RecyclerView recyclerView;

        DetailsLookup(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                if (viewHolder instanceof FavoriteViewHolder) {
                    return ((FavoriteViewHolder) viewHolder).getItemDetails();
                }
            }
            return null;
        }
    }

    static class KeyProvider extends ItemKeyProvider<Long> {

        KeyProvider(RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> adapter) {
            super(ItemKeyProvider.SCOPE_MAPPED);
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return (long) position;
        }

        @Override
        public int getPosition(@NonNull Long key) {
            long value = key;
            return (int) value;
        }
    }

    static class Details extends ItemDetailsLookup.ItemDetails<Long> {

        long position;

        Details() {
        }

        @Override
        public int getPosition() {
            return (int) position;
        }

        @Nullable
        @Override
        public Long getSelectionKey() {
            return position;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return false;
        }

        @Override
        public boolean inDragRegion(@NonNull MotionEvent e) {
            return true;
        }
    }
}
