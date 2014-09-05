package com.example.myapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.fragment.BaseFragment;
import com.example.myapp.fragment.PlanFragment;
import com.example.myapp.fragment.RecordFragment;
import com.example.myapp.fragment.TodayFragment;
import com.mirko.tbv.TabBarView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static final int TODAY = 0, PLAN = 1, RECORD = 2;

    View btn1, btn2, btn3;

    private TabBarView tabBarView;

    SectionsPagerAdapter mSectionsPagerAdapter;

    FloatingActionButton rightLowerButton;

    FloatingActionMenu rightLowerMenu;

    //    private RadioGroup mRG;
    private List<BaseFragment> mFragments = new ArrayList<BaseFragment>();

    //private FragmentTabAdapter mTabAdapter;

    private TomatoOpenHelper mOpenHelper;

    private ViewPager mViewPager;

    private AlarmManager mAlarmManager;

    public TomatoOpenHelper getOpenHelper() {
        return mOpenHelper;
    }

    public void switchTo(int dest) {
        mViewPager.setCurrentItem(dest, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);

        LayoutInflater inflator = getLayoutInflater();
        mFragments.add(new TodayFragment(this));
        mFragments.add(new PlanFragment(this));
        mFragments.add(new RecordFragment(this));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mFragments);

        View v = inflator.inflate(R.layout.custom_ab, null);
        tabBarView = (TabBarView) v.findViewById(R.id.tab_bar);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabBarView.setViewPager(mViewPager);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(v);


        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcastIntent = new Intent(AlertReceiver.DAILY_ALERT_ACTION);
        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, 0);
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.SECOND, 10);
        if (calendar.get(Calendar.HOUR) >= AppUtil.ALERT_HOUR) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.HOUR, AppUtil.ALERT_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AppUtil.REPEAT_INTERVAL, pIntent);
        //mAlarmManager.cancel(pIntent);
        mViewPager.setPageTransformer(true, new MyPageTransformer());

        mOpenHelper = new TomatoOpenHelper(getApplicationContext());

        ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_menu));
        rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
        rlIcon2.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_delete));
        rlIcon3.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_preferences));

        btn1 = rLSubBuilder.setContentView(rlIcon1).build();
        btn2 = rLSubBuilder.setContentView(rlIcon2).build();
        btn3 = rLSubBuilder.setContentView(rlIcon3).build();

        btn1.setTag(MenuType.ADD);
        btn2.setTag(MenuType.CLEAR_OLD);
        btn3.setTag(MenuType.SETTING);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(btn1)
                .addSubActionView(btn2)
                .addSubActionView(btn3)
                .attachTo(rightLowerButton)
                .build();

//        mRG = (RadioGroup) findViewById(R.id.tabs_rg);
//        mTabAdapter = new FragmentTabAdapter(this, mFragments, R.id.tab_content, mRG);
//
//        mTabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
//            @Override
//            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch ((MenuType) v.getTag()) {
            case ADD:
                mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).actionAddEvent();
                rightLowerMenu.close(true);
                break;
            case CLEAR_OLD:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.if_delete_old)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((PlanFragment) mSectionsPagerAdapter.getItem(PLAN)).clearOldEvent();
                                Toast.makeText(MainActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                rightLowerMenu.close(true);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
                break;
            case SETTING:
                rightLowerMenu.close(true);
                break;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter implements TabBarView.IconTabProvider {
        private List<BaseFragment> fragments;

        private int[] tab_icons = {android.R.drawable.star_off,
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
