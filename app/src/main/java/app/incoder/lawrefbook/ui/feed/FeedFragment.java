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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.List;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.sqlite.Sqlite3Dao;
import app.incoder.lawrefbook.storage.Category;
import app.incoder.lawrefbook.storage.Law;

/**
 * Feed
 *
 * @author : Jerry xu
 * @since : 2022/4/30 01:45
 */
public class FeedFragment extends Fragment {

    private static final String CATEGORY = "category";
    private Category mCategory;
    private List<Law> laws;
    private FeedAdapter mAdapter;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance(Category category) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putSerializable(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public void changeLawre(List<Law> data) {
        if (mAdapter == null) {
            mAdapter = new FeedAdapter(requireActivity());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FeedDiffCallBack(data, laws));
            diffResult.dispatchUpdatesTo(mAdapter);
        }
        mAdapter.setData(mCategory, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = (Category) getArguments().getSerializable(CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new FeedAdapter(requireContext());
        laws = Sqlite3Dao.lawList(requireContext(), mCategory.getId());
        mAdapter.setData(mCategory, laws);
        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(mAdapter);
    }
}