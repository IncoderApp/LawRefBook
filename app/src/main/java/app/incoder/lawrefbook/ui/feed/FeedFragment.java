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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.divider.MaterialDividerItemDecoration;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.model.Lawre;

/**
 * Feed
 *
 * @author : Jerry xu
 * @since : 2022/4/30 01:45
 */
public class FeedFragment extends Fragment {

    private static final String CATEGORY = "category";
    private Lawre category;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category category.
     * @return A new instance of fragment FeedFragment.
     */
    public static FeedFragment newInstance(Lawre category) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putSerializable(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public static void changeLawre(Lawre data) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = (Lawre) getArguments().getSerializable(CATEGORY);
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
        FeedAdapter adapter = new FeedAdapter(requireActivity(), category, requireContext());
        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(adapter);
    }
}