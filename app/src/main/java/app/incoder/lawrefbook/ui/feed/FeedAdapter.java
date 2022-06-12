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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.storage.Category;
import app.incoder.lawrefbook.storage.Law;
import app.incoder.lawrefbook.ui.content.ContentActivity;

/**
 * FeedAdapter
 *
 * @author : Jerry xu
 * @since : 2022/5/1 10:53
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<Law> mLaw;
    private final Context mContext;
    private Category mCategory;
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;

    public void setData(Category category, List<Law> data) {
        mLaw = data;
        this.mCategory = category;
    }

    public FeedAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_EMPTY) {
            view = inflater.inflate(R.layout.empty_view, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_feed, parent, false);
        }
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
//        if (mLawre != null && mLawre.getLaws().size() > 0) {
//            String title = mLawre.getLaws().get(position).getName();
//            String filename = mLawre.getLaws().get(position).getFilename();
//            String articleId = mLawre.getLaws().get(position).getId();
//            String path;
//            if (filename == null) {
//                path = mLawre.getFolder() + "/" + title + ".md";
//            } else {
//                path = mLawre.getFolder() + "/" + filename + ".md";
//            }
//            holder.mTitle.setText(title);
//            holder.itemView.setOnClickListener(v ->
//                    mContext.startActivity(new Intent(mContext, ContentActivity.class)
//                            .putExtra(ContentActivity.mPath, path)
//                            .putExtra(ContentActivity.mFolder, mLawre.getFolder())
//                            .putExtra(ContentActivity.mArticleId, articleId)
//                            .putExtra(ContentActivity.mTitle, title))
//            );
//            // transition animation
//            /*holder.itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, ContentActivity.class)
//                        .putExtra(ContentActivity.mPath, path)
//                        .putExtra(ContentActivity.mFolder, mLawre.getFolder())
//                        .putExtra(ContentActivity.mArticleId, articleId)
//                        .putExtra(ContentActivity.mTitle, title);
//                ActivityOptions options =
//                        ActivityOptions.makeSceneTransitionAnimation(mActivity, v, "shared_element_end_root");
//                mContext.startActivity(intent, options.toBundle());
//            });*/
//        }
        if (mLaw != null && mLaw.size() > 0) {
            Law law = mLaw.get(position);
            holder.mTitle.setText(law.getName());
            String path;
            if (law.getFilename() == null) {
                path = mCategory.getFolder() + "/" + law.getName() + ".md";
            } else {
                path = mCategory.getFolder() + "/" + law.getFilename() + ".md";
            }
            holder.itemView.setOnClickListener(v ->
                    mContext.startActivity(new Intent(mContext, ContentActivity.class)
                            .putExtra(ContentActivity.mPath, path)
                            .putExtra(ContentActivity.mFolder, mCategory.getFolder())
                            .putExtra(ContentActivity.mArticleId, law.getId())
                            .putExtra(ContentActivity.mTitle, law.getName()))
            );
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mLaw.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (mLaw.size() > 0) {
            return mLaw.size();
        } else {
            return 1;
        }
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
        }
    }
}
