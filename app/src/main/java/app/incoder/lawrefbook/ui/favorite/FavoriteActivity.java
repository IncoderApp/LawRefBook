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

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.storage.libraries.Libraries;
import app.incoder.lawrefbook.storage.libraries.LibrariesViewModel;

/**
 * FavoriteActivity
 *
 * @author : Jerry xu
 * @since : 2022/5/1 11:45
 */
public class FavoriteActivity extends AppCompatActivity implements ActionMode.Callback {

    private ActionMode actionMode;
    private FavoriteAdapter mAdapter;
    private LibrariesViewModel mViewModel;
    private LiveData<List<Libraries>> mLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 实际上 targetSdk >= 34 默认已启用，但显式调用更清晰
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        mViewModel = new ViewModelProvider(this).get(LibrariesViewModel.class);
        LinearLayout contentMain = findViewById(R.id.ll_favorite);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(contentMain, (v, insets) -> {
            // 获取状态栏高度（仅当系统提供 insets 时才生效）
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), statusBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        RecyclerView mRecyclerView = findViewById(R.id.rv_favorite);
        setUpRecyclerView(mRecyclerView);
    }

    private SelectionTracker<Long> selectionTracker;

    private void setUpRecyclerView(RecyclerView mRecyclerView) {
        mLiveData = mViewModel.getAllLibrariesLive();

        mAdapter = new FavoriteAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mLiveData.observe(this, libraries -> {
            mAdapter.setLibraries(libraries);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FavoriteDiffCallBack(libraries, mLiveData.getValue()));
            diffResult.dispatchUpdatesTo(mAdapter);
        });

        selectionTracker =
                new SelectionTracker.Builder<>(
                        "card_selection",
                        mRecyclerView,
                        new FavoriteAdapter.KeyProvider(mAdapter),
                        new FavoriteAdapter.DetailsLookup(mRecyclerView),
                        StorageStrategy.createLongStorage())
                        .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                        .build();

        mAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(
                new SelectionTracker.SelectionObserver<>() {
                    @Override
                    public void onSelectionChanged() {
                        if (!selectionTracker.getSelection().isEmpty()) {
                            if (actionMode == null) {
                                actionMode = startSupportActionMode(FavoriteActivity.this);
                            } else {
                                String title = String.format(getString(R.string.select_count), selectionTracker.getSelection().size());
                                actionMode.setTitle(title);
                            }
                        } else if (actionMode != null) {
                            actionMode.finish();
                        }
                    }
                });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_delete) {
            Selection<Long> selection = selectionTracker.getSelection();
            List<Libraries> value = mLiveData.getValue();
            for (Long id : selection) {
                if (value != null && !value.isEmpty()) {
                    Libraries libraries = value.get(id.intValue());
                    mViewModel.delete(libraries);
                }
            }
            Toast.makeText(this, "成功删除", Toast.LENGTH_SHORT).show();
            onDestroyActionMode(actionMode);
            return true;
        } else if (menuItem.getItemId() == R.id.menu_cancel) {
            onDestroyActionMode(actionMode);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        selectionTracker.clearSelection();
        this.actionMode = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}