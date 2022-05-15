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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.model.Lawre;
import app.incoder.lawrefbook.ui.content.ContentActivity;

/**
 * FeedAdapter
 *
 * @author : Jerry xu
 * @since : 2022/5/1 10:53
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final Lawre mLawre;
    private final Context mContext;
    private Activity mActivity;

    public FeedAdapter(Lawre lawre, Context context) {
        this.mLawre = lawre;
        this.mContext = context;
    }

    public FeedAdapter(Activity activity, Lawre lawre, Context context) {
        this.mActivity = activity;
        this.mLawre = lawre;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        String title = mLawre.getLaws().get(position).getName();
        String filename = mLawre.getLaws().get(position).getFilename();
        String articleId = mLawre.getLaws().get(position).getId();
        String path;
        if (filename == null) {
            path = mLawre.getFolder() + "/" + title + ".md";
        } else {
            path = mLawre.getFolder() + "/" + filename + ".md";
        }
        holder.mTitle.setText(title);
        holder.itemView.setOnClickListener(v ->
                mContext.startActivity(new Intent(mContext, ContentActivity.class)
                        .putExtra(ContentActivity.mPath, path)
                        .putExtra(ContentActivity.mFolder, mLawre.getFolder())
                        .putExtra(ContentActivity.mArticleId, articleId)
                        .putExtra(ContentActivity.mTitle, title))
        );
        /*holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ContentActivity.class)
                    .putExtra(ContentActivity.mPath, path)
                    .putExtra(ContentActivity.mTitle, title);
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(mActivity, v, "shared_element_end_root");
            mContext.startActivity(intent, options.toBundle());
        });*/

    }


    @Override
    public int getItemCount() {
        if (mLawre != null) {
            return mLawre.getLaws().size();
        } else {
            return 0;
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
