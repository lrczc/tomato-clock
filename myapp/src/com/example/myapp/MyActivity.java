package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.Button;

public class MyActivity extends Activity implements View.OnClickListener {

    private AnalogClock mAnalogClock;

    private Button mBtnPlan, mBtnToday, mBtnRecord;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mAnalogClock = (AnalogClock) findViewById(R.id.analog_clock);

        mBtnPlan = (Button) findViewById(R.id.btn_plan);
        mBtnToday = (Button) findViewById(R.id.btn_today);
        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnPlan.setOnClickListener(this);
        mBtnToday.setOnClickListener(this);
        mBtnRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_plan:
                break;
            case R.id.btn_today:
                startActivity(new Intent(this, TodayActivity.class));
                break;
            case R.id.btn_record:
                break;
        }
    }
}
