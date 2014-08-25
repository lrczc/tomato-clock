package com.example.myapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.example.myapp.adapter.FragmentTabAdapter;
import com.example.myapp.database.OpenHelper;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.fragment.PlanFragment;
import com.example.myapp.fragment.RecordFragment;
import com.example.myapp.fragment.TodayFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private RadioGroup mRG;
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    private FragmentTabAdapter mTabAdapter;

    private TomatoOpenHelper mOpenHelper;

    public TomatoOpenHelper getOpenHelper() {
        return mOpenHelper;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.today_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mOpenHelper = new TomatoOpenHelper(getApplicationContext());

        mFragments.add(new TodayFragment(this));
        mFragments.add(new PlanFragment(this));
        mFragments.add(new RecordFragment(this));

        mRG = (RadioGroup) findViewById(R.id.tabs_rg);

        mTabAdapter = new FragmentTabAdapter(this, mFragments, R.id.tab_content, mRG);

        mTabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
            }
        });
    }

}
