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

package app.incoder.lawrefbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import app.incoder.lawrefbook.sqlite.Sqlite3Dao;
import app.incoder.lawrefbook.storage.Category;
import app.incoder.lawrefbook.storage.Law;
import app.incoder.lawrefbook.storage.channel.Channel;
import app.incoder.lawrefbook.storage.channel.ChannelViewModel;
import app.incoder.lawrefbook.ui.channel.ChannelSheetFragment;
import app.incoder.lawrefbook.ui.favorite.FavoriteActivity;
import app.incoder.lawrefbook.ui.feed.FeedFragment;
import app.incoder.lawrefbook.ui.settings.SettingsActivity;

/**
 * LawRefBook MainActivity
 *
 * @author : Jerry xu
 * @since : 2022/4/30 01:45
 */
public class MainActivity extends AppCompatActivity {

    private List<FeedFragment> mFragmentList;
    private TabLayout mTabLayout;
    private TabLayoutMediator tabLayoutMediator;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;
    private ViewPager2 viewPager2;
    private String queryText;
    private List<Category> mCategories;
    private ChannelSheetFragment channelSheetFragment;
    private ChannelViewModel mViewModel;
    private LiveData<List<Channel>> mLiveData;
    private FragmentStateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        mCategories = Sqlite3Dao.categoryList(this);
        setContentView(R.layout.activity_main);
        LinearLayout contentMain = findViewById(R.id.ll_main);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager2 = findViewById(R.id.vp_content);
        mTabLayout = findViewById(R.id.tabLayout);

        ViewCompat.setOnApplyWindowInsetsListener(contentMain, (v, insets) -> {
            // 获取状态栏高度（仅当系统提供 insets 时才生效）
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), statusBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        liveDataObserve();
        findViewById(R.id.btn_channel).setOnClickListener(v -> {
            if (channelSheetFragment == null) {
                channelSheetFragment = ChannelSheetFragment.newInstance();
                // 设置关闭监听器
                channelSheetFragment.setOnBottomSheetDismissListener(new ChannelSheetFragment.OnBottomSheetDismissListener() {
                    @Override
                    public void onBottomSheetDismissed() {

                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onBottomSheetConfirmed(List<Category> categoriesSorted) {
                        if (!categoriesSorted.isEmpty()) {
                            mCategories.clear();
                            mFragmentList.clear();
                            mCategories = categoriesSorted;
                        }
                        updateUI(false);
                    }
                });
            }
            channelSheetFragment.show(getSupportFragmentManager(), "chanel");
        });
    }

    private void updateUI(boolean firstLoad) {
        // 清理之前的资源
        if (!firstLoad) {
            cleanUpPreviousResources();
        }

        mFragmentList = new ArrayList<>(mCategories.size());
        mCategories.forEach(t -> mFragmentList.add(FeedFragment.newInstance(t)));
        adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                if (mFragmentList == null || mFragmentList.isEmpty()) {
                    return 0;
                } else {
                    return mFragmentList.size();
                }
            }
        };
        viewPager2.setAdapter(adapter);
        viewPager2.setOffscreenPageLimit(mFragmentList.size());

        tabLayoutMediator = new TabLayoutMediator(mTabLayout, viewPager2, true, (tab, position) -> {
            tab.setText(mCategories.get(position).getName());
            if (queryText != null && !queryText.isEmpty()) {
                searchData(queryText);
            }
        });
        tabLayoutMediator.attach();

        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (queryText != null && !queryText.isEmpty()) {
                    searchData(queryText);
                }
            }
        };
        viewPager2.registerOnPageChangeCallback(pageChangeCallback);
    }

    private void cleanUpPreviousResources() {
        // 解绑之前的 TabLayoutMediator
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
            tabLayoutMediator = null;
        }

        // 移除之前的页面变更回调
        if (pageChangeCallback != null) {
            viewPager2.unregisterOnPageChangeCallback(pageChangeCallback);
            pageChangeCallback = null;
        }

        // 清除旧的 Fragment 列表（防止内存泄漏）
        if (mFragmentList != null) {
            mFragmentList.clear();
            mFragmentList = null;
        }

        // 重置适配器
        if (adapter != null) {
            adapter = null;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void liveDataObserve() {
        mLiveData = mViewModel.getAllChannelsLive();
        mLiveData.observe(this, channels -> {
            if (channels == null || channels.isEmpty()) {
                // 加载 sqlite 中的数据
                Log.w("MainActivity", "sqlite " + channels + " is null");
            } else {
                // 以存储的内容为准
                List<Category> collect = channels.stream().filter(Channel::isShow)
                        .sorted(Comparator.comparing(Channel::getSortNo))
                        .map(x -> {
                            Category category = Category.builder().build();
                            Category originCategory = mCategories.stream().filter(c -> c.getName().equals(x.getCategoryName())).findFirst().get();
                            category.setId(originCategory.getId());
                            category.setFolder(originCategory.getFolder());
                            category.setName(x.getCategoryName());
                            return category;
                        }).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    mCategories.clear();
                    mCategories.addAll(collect);
                    if (mFragmentList != null && !mFragmentList.isEmpty()) {
                        mFragmentList.clear();
                    }
                }
            }
            updateUI(true);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUpPreviousResources();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        // AppCompatDelegate
        switch (currentNightMode) {
            // 浅色
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            // 深色
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // load the file of menu that you created
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            SearchView searchView = (SearchView) item.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        queryText = query;
                        searchData(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()) {
                            queryText = "";
                        } else {
                            queryText = newText;
                        }
                        searchData(queryText);
                        return true;
                    }
                });
            }
        } else if (id == R.id.menu_collections) {
            startActivity(new Intent(this, FavoriteActivity.class));
            return true;
        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchData(String query) {
        Category category = mCategories.get(mTabLayout.getSelectedTabPosition());
        List<Law> laws = Sqlite3Dao.lawList(this, category.getId());
        List<Law> data = laws.stream().filter(t -> t.getName().contains(query)).collect(Collectors.toList());
        mFragmentList.get(mTabLayout.getSelectedTabPosition()).changeLawRef(data);
    }
}