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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.model.Article;
import app.incoder.lawrefbook.toc.FileBean;
import app.incoder.lawrefbook.toc.TreeListViewAdapter;

/**
 * CatalogSheetFragment
 *
 * @author : Jerry xu
 * @since : 2022/5/1 21:26
 */
public class CatalogSheetFragment extends BottomSheetDialogFragment {

    private static final String ARTICLE_INFO = "article_info";
    private TreeListViewAdapter<FileBean> mAdapter;
    private List<FileBean> mCatalogList;

    public static CatalogSheetFragment newInstance(Article article) {
        CatalogSheetFragment mBottomSheet = new CatalogSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARTICLE_INFO, article);
        mBottomSheet.setArguments(args);
        mBottomSheet.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
        return mBottomSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCatalogList = new ArrayList<>();
            Article article = (Article) getArguments().getSerializable(ARTICLE_INFO);
            mCatalogList = article.getToc().stream().map(t -> FileBean.builder().id(t.getId())
                    .parentId(t.getParentId())
                    .name(t.getTitle())
                    .position(t.getPosition()).build()).collect(Collectors.toList());
            mCatalogList.forEach(System.out::println);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sheet_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView mTree = requireView().findViewById(R.id.lv_toc);
        try {
            mAdapter = new SimpleTreeAdapter<>(mTree, requireContext(), mCatalogList, 0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mTree.setAdapter(mAdapter);
        mAdapter.setOnTreeNodeClickListener((node, position) -> {
            if (node.isLeaf()) {
                Toast.makeText(getContext(), node.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
