package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.Event;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shizhao.czc on 2014/8/26.
 */
public class DetailEventActivity extends Activity implements IFOnEventFetchListener {

    private EditText mEtEventName;

    private Event mEvent;

    private Spinner mSpEventTime, mSpSound;

    private TextView mTvPlanTime;

    private ArrayAdapter<String> mTimeAdapter, mSoundAdapter;

    private ImageView mBtnStartEvent;

    private TomatoOpenHelper mOpenHelper;

    private Database mDb;

    public static final int MSG_UPDATE_EVENT = 0x01;

    private EventHandler mHandler;

    @Override
    public void onEventFetch(Event event) {
        mEvent = event;
        mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
    }

    class EventHandler extends Handler {

        SoftReference<DetailEventActivity> softReference;

        public EventHandler(DetailEventActivity activity) {
            softReference = new SoftReference<DetailEventActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_EVENT:
                    mEtEventName.setText(mEvent.getEventName());
                    mSpEventTime.setSelection(mEvent.getTime(), true);
                    mSpSound.setSelection(mEvent.getSound(), true);
                    SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd");
                    String planTime = dateformat1.format(new Date(mEvent.getPlanTime()));
                    String today = dateformat1.format(new Date());
                    if (today.equals(planTime)) {
                        mTvPlanTime.setText(R.string.today);
                    } else {
                        mTvPlanTime.setText(planTime);
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_event_layout);

        mOpenHelper = new TomatoOpenHelper(getApplicationContext());
        mDb = new Database(mOpenHelper);

        mHandler = new EventHandler(this);

        Intent intent = getIntent();
        long eventId = intent.getExtras().getLong("event_id");

        mEvent = new Event();
        new LoadTask(mDb, this).execute(eventId);

        mBtnStartEvent = (ImageView) findViewById(R.id.btn_start);
        mEtEventName = (EditText) findViewById(R.id.et_event_name);
        mTvPlanTime = (TextView) findViewById(R.id.plan_time);
        mSpEventTime = (Spinner) findViewById(R.id.time_picker);
        mTimeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AppUtil.TIME_TITLE);
        mTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpEventTime.setAdapter(mTimeAdapter);
        mSpEventTime.setSelection(0, true);
        mSpEventTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mEvent.getTime() != position) {
                    mEvent.setTime(position);
                    new UpdateTask(mDb).execute(mEvent);
                    mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpEventTime.setVisibility(View.VISIBLE);

        mSpSound = (Spinner) findViewById(R.id.sound_picker);
        mSoundAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AppUtil.SOUND_TITLE);
        mSoundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpSound.setAdapter(mSoundAdapter);
        mSpSound.setSelection(0, true);
        mSpSound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mEvent.getSound() != position) {
                    mEvent.setSound(position);
                    new UpdateTask(mDb).execute(mEvent);
                    mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpSound.setVisibility(View.VISIBLE);
    }

    static class LoadTask extends AsyncTask<Long, Void, Event> {

        private Database mDb;

        private IFOnEventFetchListener listener;

        LoadTask(Database mDb, IFOnEventFetchListener listener) {
            this.mDb = mDb;
            this.listener = listener;
        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            if (event != null) {
                listener.onEventFetch(event);
            }
        }

        @Override
        protected Event doInBackground(Long... params) {
            return mDb.getEvent(params[0]);
        }
    }

    static class UpdateTask extends AsyncTask<Event, Void, Void> {

        Database mDb;

        UpdateTask(Database mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Void doInBackground(Event... params) {
            mDb.updateEvent(params[0]);
            return null;
        }
    }
}