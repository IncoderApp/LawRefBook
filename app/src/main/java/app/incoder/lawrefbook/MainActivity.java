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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import app.incoder.lawrefbook.model.Lawre;
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
    //    private LibrariesViewModel mViewModel;
    private TabLayout mTabLayout;
    private List<Lawre> data;
    private String queryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mViewModel = new ViewModelProvider(this).get(LibrariesViewModel.class);
        mTabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.vp_content);
        data = LawRefBookRepository.getData(this);
//        List<String> catalog = LawRefBookRepository.getCatalog(this);
        mFragmentList = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
//            mFragmentList.add(FeedFragment.newInstance(catalog.get(i)));
            mFragmentList.add(FeedFragment.newInstance(data.get(i)));
        }
        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return mFragmentList.size();
            }
        });
//        new TabLayoutMediator(mTabLayout, viewPager2, (tab, position) -> tab.setText(data.get(position).getCategory())).attach();

        viewPager2.setOffscreenPageLimit(mFragmentList.size());
        new TabLayoutMediator(mTabLayout, viewPager2, true, (tab, position) -> {
            tab.setText(data.get(position).getCategory());
            if (queryText != null && queryText.length() > 0) {
                searchData(queryText);
            }
        }).attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (queryText != null && queryText.length() > 0) {
                    searchData(queryText);
                }
            }
        });
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
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    queryText = query;
                    searchData(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.length() < 1) {
                        queryText = "";
                        Lawre lawre = data.get(mTabLayout.getSelectedTabPosition());
                        mFragmentList.get(mTabLayout.getSelectedTabPosition()).changeLawre(lawre);
                    } else {
                        queryText = newText;
                        // clear
                        searchData(newText);
                    }
                    return true;
                }
            });
//            searchView.setOnCloseListener(() -> {
//                return true;
//            });
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
        Lawre lawre = data.get(mTabLayout.getSelectedTabPosition());
        List<Lawre.LawsBean> laws = lawre.getLaws();
        Lawre result = new Lawre();
        List<Lawre.LawsBean> searchResult = new ArrayList<>();
        for (Lawre.LawsBean law : laws) {
            if (law.getName().contains(query)) {
                searchResult.add(law);
            }
        }
        result.setCategory(lawre.getCategory());
        result.setFolder(lawre.getFolder());
        result.setId(lawre.getId());
        result.setLinks(lawre.getLinks());
        result.setIsSubFolder(lawre.getIsSubFolder());
        result.setGroup(lawre.getGroup());
        result.setLaws(searchResult);
        mFragmentList.get(mTabLayout.getSelectedTabPosition()).changeLawre(result);
    }
}