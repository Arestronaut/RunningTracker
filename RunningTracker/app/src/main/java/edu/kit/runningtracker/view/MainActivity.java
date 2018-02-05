package edu.kit.runningtracker.view;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.kit.runningtracker.R;
import edu.kit.runningtracker.run.RunFragment;
import edu.kit.runningtracker.settings.SettingsFragment;

/**
 * @author Josh Romanowski
 */

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private CustomPagerAdapter adapter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Toolbar actions = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(actions);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        setupTablayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);

        Drawable btDrawable = menu.findItem(R.id.action_bluetooth).getIcon();

        if (btDrawable != null) {
            btDrawable.mutate();
            btDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        }

        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bluetooth:
                //viewPager.setAdapter(adapter);

                for (Fragment fragment : this.adapter.mFragmentList) {
                    if (fragment instanceof RunFragment) {
                        ((RunFragment) fragment).reinitiateBluetooth();
                    }
                }

                return true;
            default:
                return false;
        }
    }

    public void setMenuEnabled(Boolean enabled) {
        if (this.mMenu != null) {
            MenuItem item = this.mMenu.getItem(0);
            item.setEnabled(enabled);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        this.adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RunFragment(), getString(R.string.action_run));
        adapter.addFragment(new SettingsFragment(), getString(R.string.action_settings));
        viewPager.setAdapter(adapter);
    }

    private void setupTablayout() {
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    class CustomPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        CustomPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
