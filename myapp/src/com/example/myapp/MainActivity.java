package com.example.myapp;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.example.myapp.adapter.FragmentTabAdapter;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.fragment.BaseFragment;
import com.example.myapp.fragment.PlanFragment;
import com.example.myapp.fragment.RecordFragment;
import com.example.myapp.fragment.TodayFragment;
import com.mirko.tbv.TabBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

    private TabBarView tabBarView;

    SectionsPagerAdapter mSectionsPagerAdapter;

//    private RadioGroup mRG;
    private List<BaseFragment> mFragments = new ArrayList<BaseFragment>();

    //private FragmentTabAdapter mTabAdapter;

    private TomatoOpenHelper mOpenHelper;

    private ViewPager mViewPager;

    public TomatoOpenHelper getOpenHelper() {
        return mOpenHelper;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.today_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).actionAddEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);

        LayoutInflater inflator =
                (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflator.inflate(R.layout.custom_ab, null);
        tabBarView = (TabBarView) v.findViewById(R.id.tab_bar);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(v);

        mOpenHelper = new TomatoOpenHelper(getApplicationContext());

        mFragments.add(new TodayFragment(this));
        mFragments.add(new PlanFragment(this));
        mFragments.add(new RecordFragment(this));


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mFragments);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabBarView.setViewPager(mViewPager);

//        mRG = (RadioGroup) findViewById(R.id.tabs_rg);
//        mTabAdapter = new FragmentTabAdapter(this, mFragments, R.id.tab_content, mRG);
//
//        mTabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
//            @Override
//            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
//            }
//        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements TabBarView.IconTabProvider {
        private List<BaseFragment> fragments;

        private int[] tab_icons={android.R.drawable.star_off,
                android.R.drawable.star_off,
                android.R.drawable.star_off,
        };

        public SectionsPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public BaseFragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getPageIconResId(int position) {
            return tab_icons[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.today);
                case 1:
                    return getString(R.string.plan);
                case 2:
                    return getString(R.string.record);
            }
            return null;
        }

    }

}
