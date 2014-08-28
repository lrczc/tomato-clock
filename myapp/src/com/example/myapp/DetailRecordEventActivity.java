package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.RecordEvent;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shizhao.czc on 2014/8/28.
 */
public class DetailRecordEventActivity extends Activity implements IFOnRecordEventFetchListener {

    private RecordEvent mRecordEvent;

    TextView mTvEventName, mTvTime, mTvCompleteTime, mTvSound;

    public static final int MSG_UPDATE_EVENT = 0x01;

    private EventHandler mHandler;
    private TomatoOpenHelper mOpenHelper;
    private Database mDb;

    @Override
    public void onEventFetch(RecordEvent event) {
        mRecordEvent = event;
        mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
    }

    class EventHandler extends Handler {

        SoftReference<DetailRecordEventActivity> softReference;

        public EventHandler(DetailRecordEventActivity activity) {
            softReference = new SoftReference<DetailRecordEventActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyy-MM-dd");
            switch (msg.what) {
                case MSG_UPDATE_EVENT: {
                    mTvEventName.setText(mRecordEvent.getEventName());
                    mTvTime.setText(AppUtil.TIME_TITLE[mRecordEvent.getTime()]);
                    String completeTime = dateFormat1.format(new Date(mRecordEvent.getCompleteTime()));
                    String today = dateFormat1.format(new Date());
                    if (today.equals(completeTime)) {
                        mTvCompleteTime.setText(R.string.today);
                    } else {
                        mTvCompleteTime.setText(completeTime);
                    }
                    mTvSound.setText(AppUtil.SOUND_TITLE[mRecordEvent.getSound()]);
                    break;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_record_event_layout);

        mHandler = new EventHandler(this);

        mTvEventName = (TextView) findViewById(R.id.event_name);
        mTvTime = (TextView) findViewById(R.id.time);
        mTvCompleteTime = (TextView) findViewById(R.id.complete_time);
        mTvSound = (TextView) findViewById(R.id.sound_type);

        mOpenHelper = new TomatoOpenHelper(getApplicationContext());
        mDb = new Database(mOpenHelper);

        Intent intent = getIntent();
        long eventId = intent.getExtras().getLong("event_id");
        new LoadTask(mDb, this).execute(eventId);
    }

    static class LoadTask extends AsyncTask<Long, Void, RecordEvent> {

        private Database mDb;

        private IFOnRecordEventFetchListener listener;

        LoadTask(Database mDb, IFOnRecordEventFetchListener listener) {
            this.mDb = mDb;
            this.listener = listener;
        }

        @Override
        protected void onPostExecute(RecordEvent event) {
            super.onPostExecute(event);
            if (event != null) {
                listener.onEventFetch(event);
            }
        }

        @Override
        protected RecordEvent doInBackground(Long... params) {
            return mDb.getRecordEvent(params[0]);
        }
    }
}