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

package app.incoder.lawrefbook.ui.content;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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

/**
 * ContentAdapter
 *
 * @author : Jerry xu
 * @since : 2022/5/1 19:46
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private List<String> mContent;
    private String queryText;
    private SelectionTracker<Long> selectionTracker;

    public ContentAdapter(List<String> content) {
        this.mContent = content;
    }

    public void setData(List<String> content, String queryText) {
        this.mContent = content;
        this.queryText = queryText;
    }

    public List<String> getData() {
        return mContent;
    }


    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        String item = mContent.get(position);
        holder.bind(item, holder.mTitle.getContext(), position);
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mCardView;
        private final Details details;
        TextView mTitle;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.mcv_content);
            mTitle = itemView.findViewById(R.id.tv_content);
            details = new Details();
        }

        private void bind(String item, Context context, int position) {
            details.position = position;
            SpannableString spannableString = new SpannableString(item);
            if (queryText != null && queryText.length() > 0 && item.contains(queryText)) {
                // search
                int temp = item.indexOf(queryText);
                while (temp != -1) {
                    // highlighted search
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getColor(R.color.searchHighlight));
                    spannableString.setSpan(colorSpan, temp, temp + queryText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    RelativeSizeSpan sizeSpan11 = new RelativeSizeSpan(1.1f);
                    spannableString.setSpan(sizeSpan11, temp, temp + queryText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    temp = item.indexOf(queryText, temp + 1);
                }
            }
            // mark
            if (item.matches("(第.+?条)( *)([\\s\\S]*)")) {
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                int end = item.indexOf("条") + 1;
                if (end > 0) {
                    spannableString.setSpan(styleSpan, 0, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            mTitle.setText(spannableString);
            if (selectionTracker != null) {
                bindSelectedState();
            }
        }

        private void bindSelectedState() {
            mCardView.setChecked(selectionTracker.isSelected(details.getSelectionKey()));
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
                if (viewHolder instanceof ContentViewHolder) {
                    return ((ContentViewHolder) viewHolder).getItemDetails();
                }
            }
            return null;
        }
    }

    static class KeyProvider extends ItemKeyProvider<Long> {

        KeyProvider(RecyclerView.Adapter<ContentAdapter.ContentViewHolder> adapter) {
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
