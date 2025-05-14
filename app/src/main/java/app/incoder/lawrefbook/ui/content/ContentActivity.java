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

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.databinding.ActivityContentBinding;
import app.incoder.lawrefbook.model.Article;
import app.incoder.lawrefbook.model.Classify;
import app.incoder.lawrefbook.model.Content;
import app.incoder.lawrefbook.storage.Libraries;
import app.incoder.lawrefbook.storage.LibrariesViewModel;
import app.incoder.lawrefbook.ui.catalog.CatalogSheetFragment;
import app.incoder.lawrefbook.ui.favorite.FavoriteActivity;
import app.incoder.lawrefbook.util.Image;
import app.incoder.lawrefbook.util.IntentAction;
import app.incoder.lawrefbook.util.SimpleUtils;

/**
 * ContentActivity
 *
 * @author : Jerry xu
 * @since : 2022/5/1 11:45
 */
public class ContentActivity extends AppCompatActivity {

    public static String Title = "title";
    public static String Path = "path";
    public static String Article = "article";
    public static String Folder = "folder";
    public static String ArticleId = "articleId";

    protected CoordinatorLayout mCoordinatorLayout;
    private ActivityContentBinding mBinding;
    private BottomAppBar mBottomAppBar;
    private AppBarLayout mBarLayout;
    private RecyclerView mRecyclerView;
    private ContentAdapter mAdapter;
    private CatalogSheetFragment mSheetFragment;

    private Article mArticle;
    private String mTitle;
    private String mPath;
    private String mFolder;
    private String mArticleId;
    private LibrariesViewModel mViewModel;
    private FloatingActionButton mFavorite;
    private SelectionTracker<Long> mSelectionTracker;
    private List<Content> mContentList;
    private Selection<Long> selection;
    private boolean mCollected;
    private Integer mLibrariesId;
    private static final int REQUEST_CODE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LibrariesViewModel.class);
        mBinding = ActivityContentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Toolbar toolbar = mBinding.toolbar;
        mBarLayout = mBinding.appBar;
        mCoordinatorLayout = mBinding.coordinatorLayout;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mTitle = getIntent().getStringExtra(ContentActivity.Title);
        mPath = getIntent().getStringExtra(ContentActivity.Path);
        mArticleId = getIntent().getStringExtra(ContentActivity.ArticleId);
        mFolder = getIntent().getStringExtra(ContentActivity.Folder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mArticle = getIntent().getSerializableExtra(ContentActivity.Article, Article.class);
        } else {
            mArticle = (Article) getIntent().getSerializableExtra(ContentActivity.Article);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        CollapsingToolbarLayout toolBarLayout = mBinding.toolbarLayout;

        FloatingActionButton fabInfo = mBinding.officialUrl;
        mFavorite = mBinding.favorite;
        mBottomAppBar = mBinding.extend;
        mBinding.tvCount.setText(String.format(getString(R.string.word_count), mArticle.getInfo().getWordsCount()));
        if ("".equals(mArticle.getTitle())) {
            toolBarLayout.setTitle(mTitle);
        } else {
            String toolbarTitle = mArticle.getTitle();
            if (toolbarTitle.length() > 18) {
                StringBuilder stringBuffer = new StringBuilder(toolbarTitle);
                stringBuffer.replace(18, toolbarTitle.length(), "...");
                toolbarTitle = stringBuffer.toString();
            }
            toolBarLayout.setTitle(toolbarTitle);
        }

        barMenuOnClickListener();

        // TODO 文章官方地址
        fabInfo.setOnClickListener(view -> new MaterialAlertDialogBuilder(ContentActivity.this).setTitle(view.getContentDescription()).setPositiveButton(getResources().getString(R.string.i_know), null).setMessage(getResources().getString(R.string.empty_data)).show());

        mViewModel.getFavorite(mArticleId, Classify.FULL_CATEGORY.getName()).observe(this, libraries -> {
            if (!libraries.isEmpty()) {
                mLibrariesId = libraries.stream().map(Libraries::getId).findFirst().get();
                mFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                mCollected = true;
            } else {
                mFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                mCollected = false;
            }
        });

        mFavorite.setOnClickListener(view -> favoriteManager());

        setUpRecyclerView();

        setUpBottomDrawer();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectionTracker.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectionTracker.onSaveInstanceState(outState);
    }

    private void barMenuOnClickListener() {
        mBottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_share) {
                // share
                if (mSelectionTracker.getSelection().isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.select_share_content), Toast.LENGTH_SHORT).show();
                } else {
                    selection = mSelectionTracker.getSelection();
                    new MaterialAlertDialogBuilder(ContentActivity.this).setTitle(getResources().getString(R.string.menu_share)).setPositiveButton(getResources().getString(R.string.i_know), null).setItems(getResources().getStringArray(R.array.content_action), (dialog, which) -> {
                        if (which == 0) {
                            // collection
                            saveLaw(selection);
                        } else if (which == 1) {
                            // email feedback
                            emailFeedback(selection);
                        } else if (which == 2) {
                            // text copy
                            textClip(selection);
                        } else {
                            // law picture
                            if (mSelectionTracker.getSelection().size() > 3) {
                                Toast.makeText(this, "超出保存法条", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            saveLawPicture();
                        }
                        mSelectionTracker.clearSelection();
                        mBinding.tvCount.setText(String.format(getString(R.string.word_count), mArticle.getInfo().getWordsCount()));
                    }).show();
                }
            } else if (item.getItemId() == R.id.menu_collections) {
                startActivity(new Intent(ContentActivity.this, FavoriteActivity.class));
            } else if (item.getItemId() == R.id.menu_history) {
                showHistory();
            }
            return true;
        });
    }

    private void checkPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED)) {
            // Android 13及以上完整照片和视频访问权限
            doSomething();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
            // Android 14及以上部分照片和视频访问权限
            doSomething();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Android 12及以下完整本地读写访问权限
            doSomething();
        } else {
            // 无本地读写访问权限
        }
    }

    private void saveLawPicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            doSomething();
        }
    }

    private void textClip(Selection<Long> selection) {
        StringBuilder builder = new StringBuilder();
        for (Long snippetsIndex : selection) {
            String snippets = mContentList.get(snippetsIndex.intValue()).getRule();
            builder.append(snippets).append(selection.size() > 1 ? "\n" : "");
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
    }

    private void emailFeedback(Selection<Long> selection) {
        StringBuilder builder = new StringBuilder();
        for (Long snippetsIndex : selection) {
            String snippets = mContentList.get(snippetsIndex.intValue()).getRule();
            builder.append(snippets).append(selection.size() > 1 ? "\n" : "");
        }
        // open text application
        if (builder.length() > 0) {
            IntentAction.sendEmail(this, String.format(getString(R.string.content_feedback), getResources().getString(R.string.app_name)), mTitle + "\n" + mFolder + "\n" + builder, getString(R.string.author_email));
        }
    }

    private void saveLaw(Selection<Long> selection) {
        for (Long snippetsIndex : selection) {
            String snippets = mContentList.get(snippetsIndex.intValue()).getRule();
            Libraries libraries = new Libraries();
            libraries.setName(mArticle.getTitle());
            libraries.setLawsId(mArticleId);
            libraries.setArticlePath(mPath);
            libraries.setSnippetsContent(snippets);
//            libraries.setSnippetsIndex();
            libraries.setArticleFolder(mFolder);
            Date now = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            libraries.setCreateTime(f.format(now));
            libraries.setClassify(Classify.SNIPPETS_CATEGORY.getName());
            mViewModel.insertAction(libraries);
        }
        Toast.makeText(this, getResources().getString(R.string.collected), Toast.LENGTH_SHORT).show();
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.rv_text);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mContentList = mArticle.getContents();
        mAdapter = new ContentAdapter(mContentList);
        mRecyclerView.setAdapter(mAdapter);

        mSelectionTracker = new SelectionTracker.Builder<>("content_selection", mRecyclerView, new ContentAdapter.KeyProvider(mAdapter), new ContentAdapter.DetailsLookup(mRecyclerView), StorageStrategy.createLongStorage()).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build();

        mAdapter.setSelectionTracker(mSelectionTracker);
        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                if (!mSelectionTracker.getSelection().isEmpty()) {
                    String title = String.format(getString(R.string.select_count), mSelectionTracker.getSelection().size());
                    mBinding.tvCount.setText(title);
                } else {
                    mBinding.tvCount.setText(String.format(getString(R.string.word_count), mArticle.getInfo().getWordsCount()));
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void favoriteManager() {
        Libraries libraries = new Libraries();
        if (mCollected) {
            libraries.setId(mLibrariesId);
            mViewModel.delete(libraries);
            mFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            Toast.makeText(this, getResources().getString(R.string.unbookmark_articles), Toast.LENGTH_SHORT).show();
            mCollected = false;
        } else {
            libraries.setName(mArticle.getTitle());
            libraries.setLawsId(mArticleId);
            libraries.setArticlePath(mPath);
            Date now = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            libraries.setCreateTime(f.format(now));
            libraries.setArticleFolder(mFolder);
            libraries.setClassify(Classify.FULL_CATEGORY.getName());
            mViewModel.insertAction(libraries);
            mFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            Toast.makeText(this, getResources().getString(R.string.bookmark_articles), Toast.LENGTH_SHORT).show();
            mCollected = true;
        }
    }

    private void showHistory() {
        new MaterialAlertDialogBuilder(ContentActivity.this).setTitle(mArticle.getTitle()).setPositiveButton(getResources().getString(R.string.i_know), null).setItems(mArticle.getInfo().getCorrectHistory().toArray(new String[0]), null).show();
    }

    protected void setUpBottomDrawer() {
        mBottomAppBar.setNavigationOnClickListener(v -> {
            if (mArticle.getToc().isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.untitled), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSheetFragment == null) {
                mSheetFragment = CatalogSheetFragment.newInstance(mArticle, ContentActivity.this);
            }
            mSheetFragment.show(getSupportFragmentManager(), "dialog");
        });
    }

    public void smoothScrollToPosition(int position) {
        mSheetFragment.dismiss();
        mBarLayout.setExpanded(false);
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(position - 1);
        Objects.requireNonNull(mRecyclerView.getLayoutManager()).startSmoothScroll(smoothScroller);
    }

    @Override
    public void onBackPressed() {
        if (!mSelectionTracker.getSelection().isEmpty()) {
            mSelectionTracker.clearSelection();
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
        mAdapter.setData(mContentList, query);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ContentDiffCallBack(mContentList, mContentList));
        diffResult.dispatchUpdatesTo(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doSomething();
            } else {
                Toast.makeText(this, getResources().getString(R.string.obtain_permissions), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 模拟文件写入
     */
    private void doSomething() {
        StringBuilder sb = new StringBuilder();
        sb.append("《").append(mArticle.getTitle()).append("》").append("\n");
        for (Long index : selection) {
            String snippets = mContentList.get(index.intValue()).getRule();
            sb.append(snippets).append(selection.size() > 1 ? "\n" : "");
        }
        EditText editText = new EditText(this);
        editText.setText(sb.toString());
        Bitmap bitmap = Image.getBitmap(this, editText);
        boolean saveResult = SimpleUtils.saveToGallery(this, bitmap, mArticle.getTitle());
        if (saveResult) {
            Toast.makeText(this, getResources().getString(R.string.save_success), Toast.LENGTH_SHORT).show();
        }
    }
}