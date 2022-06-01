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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.storage.Libraries;
import app.incoder.lawrefbook.storage.LibrariesViewModel;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.menu_collections);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mViewModel = new ViewModelProvider(this).get(LibrariesViewModel.class);
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
                new SelectionTracker.SelectionObserver<Long>() {
                    @Override
                    public void onSelectionChanged() {
                        if (selectionTracker.getSelection().size() > 0) {
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
                if (value != null && value.size() > 0) {
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