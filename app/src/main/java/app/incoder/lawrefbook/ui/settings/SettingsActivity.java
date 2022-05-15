package app.incoder.lawrefbook.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import app.incoder.lawrefbook.R;

/**
 * SettingsActivity
 *
 * @author : Jerry xu
 * @since : 2022/5/1 11:45
 */
public class SettingsActivity extends AppCompatActivity {

    public ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            /*getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();*/
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new AboutFragment())
                    .commit();
        }
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.menu_settings);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}