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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.incoder.lawrefbook.LawRefBookRepository;
import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.databinding.ActivityContentBinding;
import app.incoder.lawrefbook.model.Article;
import app.incoder.lawrefbook.model.Classify;
import app.incoder.lawrefbook.model.Content;
import app.incoder.lawrefbook.storage.Libraries;
import app.incoder.lawrefbook.storage.LibrariesViewModel;
import app.incoder.lawrefbook.ui.catalog.CatalogSheetFragment;
import app.incoder.lawrefbook.ui.favorite.FavoriteActivity;
import app.incoder.lawrefbook.util.IntentAction;

/**
 * ContentActivity
 *
 * @author : Jerry xu
 * @since : 2022/5/1 11:45
 */
public class ContentActivity extends AppCompatActivity {

    public static String mTitle = "title";
    public static String mPath = "path";
    public static String mFolder = "folder";
    public static String mArticleId = "articleId";

    protected CoordinatorLayout coordinatorLayout;
    private ActivityContentBinding binding;
    private BottomAppBar bar;
    private RecyclerView mRecyclerView;
    private ContentAdapter mAdapter;
    private CatalogSheetFragment mSheetFragment;

    private Article article;
    private String title;
    private String path;
    private String folder;
    private String articleId;
    private LibrariesViewModel mViewModel;
    private FloatingActionButton favorite;
    private SelectionTracker<Long> selectionTracker;
    private List<Content> content;
    private boolean headerTitle;
    private boolean collected;
    private Integer librariesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LibrariesViewModel.class);
        binding = ActivityContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        coordinatorLayout = binding.coordinatorLayout;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        title = getIntent().getStringExtra(ContentActivity.mTitle);
        path = getIntent().getStringExtra(ContentActivity.mPath);
        articleId = getIntent().getStringExtra(ContentActivity.mArticleId);
        folder = getIntent().getStringExtra(ContentActivity.mFolder);

        toolbar.setNavigationOnClickListener(v -> finish());
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;

        article = LawRefBookRepository.getArticle(this, path);
        FloatingActionButton fabInfo = binding.officialUrl;
        favorite = binding.favorite;
        bar = binding.extend;
        binding.tvCount.setText(String.format(getString(R.string.word_count), article.getInfo().getWordsCount()));
        if ("".equals(article.getTitle())) {
            toolBarLayout.setTitle(title);
        } else {
            String toolbarTitle = article.getTitle();
            if (toolbarTitle.length() > 18) {
                headerTitle = true;
                StringBuilder stringBuffer = new StringBuilder(toolbarTitle);
                stringBuffer.replace(19, toolbarTitle.length(), "...");
                toolbarTitle = stringBuffer.toString();
            }
            toolBarLayout.setTitle(toolbarTitle);
        }

        barMenuOnClickListener();

        // TODO 文章官方地址
        fabInfo.setOnClickListener(view -> new MaterialAlertDialogBuilder(ContentActivity.this)
                .setTitle(view.getContentDescription())
                .setPositiveButton(getResources().getString(R.string.i_know), null)
                .setMessage(getResources().getString(R.string.empty_data))
                .show());

        mViewModel.getFavorite(articleId, Classify.FULL_CATEGORY.getName()).observe(this, libraries -> {
            if (libraries.size() > 0) {
                librariesId = libraries.stream().map(Libraries::getId).findFirst().get();
                favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                collected = true;
            } else {
                favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                collected = false;
            }
        });

        favorite.setOnClickListener(view -> favoriteManager());

        setUpRecyclerView();

        setUpBottomDrawer();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }

    private void barMenuOnClickListener() {
        bar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_share) {
                if (selectionTracker.getSelection().size() < 1) {
                    Toast.makeText(this, getResources().getString(R.string.select_share_content), Toast.LENGTH_SHORT).show();
                } else {
                    Selection<Long> selection = selectionTracker.getSelection();
                    new MaterialAlertDialogBuilder(ContentActivity.this)
                            .setTitle(getResources().getString(R.string.menu_share))
                            .setPositiveButton(getResources().getString(R.string.i_know), null)
                            .setItems(getResources().getStringArray(R.array.content_action), (dialog, which) -> {
                                if (which == 0) {
                                    // collection
                                    for (Long snippetsIndex : selection) {
                                        String snippets = content.get(snippetsIndex.intValue()).getRule();
                                        Libraries libraries = new Libraries();
                                        libraries.setName(article.getTitle());
                                        libraries.setLawsId(articleId);
                                        libraries.setArticlePath(path);
                                        libraries.setSnippetsContent(snippets);
//                                        libraries.setSnippetsIndex();
                                        libraries.setArticleFolder(folder);
                                        Date now = new Date();
                                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                                        libraries.setCreateTime(f.format(now));
                                        libraries.setClassify(Classify.SNIPPETS_CATEGORY.getName());
                                        mViewModel.insertAction(libraries);
                                    }
                                    selectionTracker.clearSelection();
                                    binding.tvCount.setText(String.format(getString(R.string.word_count), article.getInfo().getWordsCount()));
                                    Toast.makeText(this, getResources().getString(R.string.collected), Toast.LENGTH_SHORT).show();
                                } else if (which == 1) {
                                    // email feedback
                                    StringBuilder builder = new StringBuilder();
                                    for (Long snippetsIndex : selection) {
                                        String snippets = content.get(snippetsIndex.intValue()).getRule();
                                        builder.append(snippets)
                                                .append(selection.size() > 1 ? "\n" : "");
                                    }
                                    // open text application
                                    if (builder.length() > 0) {
                                        IntentAction.sendEmail(this
                                                , String.format(getString(R.string.content_feedback), getResources().getString(R.string.app_name))
                                                , title + "\n" + folder + "\n" + builder
                                                , getString(R.string.author_email));
                                    }
                                    selectionTracker.clearSelection();
                                    binding.tvCount.setText(String.format(getString(R.string.word_count), article.getInfo().getWordsCount()));
                                } else {
                                    StringBuilder builder = new StringBuilder();
                                    for (Long snippetsIndex : selection) {
                                        String snippets = content.get(snippetsIndex.intValue()).getRule();
                                        builder.append(snippets)
                                                .append(selection.size() > 1 ? "\n" : "");
                                    }
                                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData mClipData = ClipData.newPlainText("law", builder.toString());
                                    clipboardManager.setPrimaryClip(mClipData);
                                    // open text application
                                    if (builder.length() > 0) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                                        intent.setType("text/plain");
                                        startActivity(intent);
                                    }
                                    selectionTracker.clearSelection();
                                    binding.tvCount.setText(String.format(getString(R.string.word_count), article.getInfo().getWordsCount()));
                                }
                            })
                            .show();
                }
            } else if (item.getItemId() == R.id.menu_collections) {
                startActivity(new Intent(ContentActivity.this, FavoriteActivity.class));
            } else if (item.getItemId() == R.id.menu_history) {
                showHistory();
            }
            return true;
        });
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.rv_text);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        content = article.getContents();
        mAdapter = new ContentAdapter(content);
        mRecyclerView.setAdapter(mAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "content_selection",
                mRecyclerView,
                new ContentAdapter.KeyProvider(mAdapter),
                new ContentAdapter.DetailsLookup(mRecyclerView),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build();

        mAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                if (selectionTracker.getSelection().size() > 0) {
                    String title = String.format(getString(R.string.select_count), selectionTracker.getSelection().size());
                    binding.tvCount.setText(title);
                } else {
                    binding.tvCount.setText(String.format(getString(R.string.word_count), article.getInfo().getWordsCount()));
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void favoriteManager() {
        Libraries libraries = new Libraries();
        if (collected) {
            libraries.setId(librariesId);
            mViewModel.delete(libraries);
            favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            Toast.makeText(this, getResources().getString(R.string.unbookmark_articles), Toast.LENGTH_SHORT).show();
            collected = false;
        } else {
            libraries.setName(article.getTitle());
            libraries.setLawsId(articleId);
            libraries.setArticlePath(path);
            Date now = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            libraries.setCreateTime(f.format(now));
            libraries.setArticleFolder(folder);
            libraries.setClassify(Classify.FULL_CATEGORY.getName());
            mViewModel.insertAction(libraries);
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            Toast.makeText(this, getResources().getString(R.string.bookmark_articles), Toast.LENGTH_SHORT).show();
            collected = true;
        }
    }

    private void showHistory() {
        new MaterialAlertDialogBuilder(ContentActivity.this)
                .setTitle(article.getTitle())
                .setPositiveButton(getResources().getString(R.string.i_know), null)
                .setItems(article.getInfo().getCorrectHistory().toArray(new String[0]), null)
                .show();
    }

    protected void setUpBottomDrawer() {
        bar.setNavigationOnClickListener(v -> {
            if (article.getToc().size() < 1) {
                Toast.makeText(this, getResources().getString(R.string.untitled), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSheetFragment == null) {
                mSheetFragment = CatalogSheetFragment.newInstance(article);
            }
            mSheetFragment.show(getSupportFragmentManager(), "dialog");
        });
    }

    private int getBottomDataPosition() {
        return mAdapter.getItemCount() + mAdapter.getData().size() - 1;
    }

    public void smoothScrollToBottom() {
        if (mAdapter == null) {
            return;
        }
        mRecyclerView.post(() -> {
            mSheetFragment.dismiss();
            mRecyclerView.smoothScrollToPosition(getBottomDataPosition());
        });
    }

    @Override
    public void onBackPressed() {
        if (selectionTracker.getSelection().size() > 0) {
            selectionTracker.clearSelection();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // load the file of menu that you created
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    querySearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    querySearch(newText);
                    return true;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void querySearch(String query) {
        mAdapter.setData(content, query);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ContentDiffCallBack(content, content));
        diffResult.dispatchUpdatesTo(mAdapter);
    }

}