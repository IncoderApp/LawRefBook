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

package app.incoder.lawrefbook.ui.catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.incoder.lawrefbook.R;

/**
 * CatalogSheetAdapter
 *
 * @author : Jerry xu
 * @since : 2022/5/1 21:33
 */
public class CatalogSheetAdapter extends RecyclerView.Adapter<CatalogSheetAdapter.CatalogViewHolder> {

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_toc, parent, false);
        return new CatalogViewHolder(view);

//        CatalogViewHolder viewHolder;
//
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(R.layout.item_toc, parent, false);
//        viewHolder = new CatalogViewHolder(view);
//        viewHolder.icon = view.findViewById(R.id.iv_icon);
//        viewHolder.label = convertView.findViewById(R.id.tv_label);
//        view.setTag(viewHolder);
//
//        if (node.getIcon() == -1) {
//            viewHolder.icon.setVisibility(View.INVISIBLE);
//        } else {
//            viewHolder.icon.setVisibility(View.VISIBLE);
//            viewHolder.icon.setImageResource(node.getIcon());
//        }
//        viewHolder.label.setText(node.getName());
//        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class CatalogViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView label;

        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
