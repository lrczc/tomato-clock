package com.example.myapp;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.Event;
import com.example.myapp.model.RecordEvent;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shizhao.czc on 2014/8/26.
 */
public class DetailEventActivity extends Activity implements IFOnEventFetchListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "detail event";

    private EditText mEtEventName;

    private Event mEvent;

    private Spinner mSpEventTime, mSpSound;

    private TextView mTvPlanTime, mTvRealTime;

    private ArrayAdapter<String> mTimeAdapter, mSoundAdapter;

    private ImageView mBtnStartEvent;

    private TomatoOpenHelper mOpenHelper;

    private long startTime;

    private boolean starting = false;

    private Database mDb;

    private DatePickerDialog dialog;

    public static final int MSG_UPDATE_EVENT = 0x01;

    public static final int MSG_UPDATE_TIME = 0x02;

    public static final int MSG_TIMESUP = 0x03;

    static final int DELAY_TIME = 100;

    private EventHandler mHandler;

    private AlarmManager mAlarmManager;

    PendingIntent pIntent;

    @Override
    public void onEventFetch(Event event) {
        mEvent = event;
        mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date date = new Date(year-1900, monthOfYear, dayOfMonth);
        mEvent.setPlanTime(date.getTime());
        new UpdateTask(mDb).execute(mEvent);
        mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
    }

    class EventHandler extends Handler {

        SoftReference<DetailEventActivity> softReference;

        public EventHandler(DetailEventActivity activity) {
            softReference = new SoftReference<DetailEventActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("mm:ss:SSS");
            long totalTime = AppUtil.TIME[mEvent.getTime()]*60000;
            switch (msg.what) {
                case MSG_UPDATE_EVENT: {
                    mEtEventName.setText(mEvent.getEventName());
                    mSpEventTime.setSelection(mEvent.getTime(), true);
                    mSpSound.setSelection(mEvent.getSound(), true);
                    String planTime = dateFormat1.format(new Date(mEvent.getPlanTime()));
                    String today = dateFormat1.format(new Date());
                    if (today.equals(planTime)) {
                        mTvPlanTime.setText(R.string.today);
                    } else {
                        mTvPlanTime.setText(planTime);
                    }
                    if (!starting) {
                        String realTime = dateFormat2.format(new Date(totalTime));
                        mTvRealTime.setText(realTime);
                    }
                    break;
                }
                case MSG_UPDATE_TIME: {
                    long remain_time = totalTime-(System.currentTimeMillis()-startTime);
                    if (remain_time > 0) {
                        String realTime = dateFormat2.format(new Date(remain_time));
                        mTvRealTime.setText(realTime);
                        sendEmptyMessageDelayed(MSG_UPDATE_TIME, DELAY_TIME);
                    } else {
                        new CompleteTask(mDb).execute(mEvent);
                        finish();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_event_layout);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mOpenHelper = new TomatoOpenHelper(getApplicationContext());
        mDb = new Database(mOpenHelper);

        mHandler = new EventHandler(this);

        Intent intent = getIntent();
        long eventId = intent.getExtras().getLong("event_id");
        new LoadTask(mDb, this).execute(eventId);

        mBtnStartEvent = (ImageView) findViewById(R.id.btn_start);
        mBtnStartEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!starting) {
                    if (mEvent.getTime() == 0) {
                        Toast.makeText(DetailEventActivity.this, R.string.not_set_time, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    starting = true;
                    Calendar calendar=Calendar.getInstance();
                    startTime = System.currentTimeMillis();
                    calendar.setTimeInMillis(startTime);
                    calendar.add(Calendar.MINUTE, AppUtil.TIME[mEvent.getTime()]);
                    Intent broadcastIntent = new Intent(AlarmReceiver.ALARM_ALERT_ACTION);
                    broadcastIntent.putExtra("sound_res", AppUtil.SOUND[mEvent.getSound()]);
                    pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, 0);
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
                    //mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pIntent);
                    mBtnStartEvent.setImageResource(android.R.drawable.ic_media_pause);
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
                } else {
                    new AlertDialog.Builder(DetailEventActivity.this)
                            .setMessage(R.string.if_give_up)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    starting = false;
                                    mAlarmManager.cancel(pIntent);
                                    mBtnStartEvent.setImageResource(android.R.drawable.ic_media_play);
                                    mHandler.removeMessages(MSG_UPDATE_TIME);
                                    mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create().show();
                }

            }
        });
        mEtEventName = (EditText) findViewById(R.id.et_event_name);
        mTvRealTime = (TextView) findViewById(R.id.real_time);
        mTvPlanTime = (TextView) findViewById(R.id.plan_time);
        mTvPlanTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                dialog = new DatePickerDialog(DetailEventActivity.this, DetailEventActivity.this, year, month, day);
                dialog.show();
            }
        });
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

    static class CompleteTask extends AsyncTask<Event, Void, Void> {

        Database mDb;

        CompleteTask(Database mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Void doInBackground(Event... params) {
            RecordEvent recordEvent = new RecordEvent(params[0]);
            recordEvent.setCompleteTime(System.currentTimeMillis());
            mDb.insertRecordEvent(recordEvent);
            mDb.deleteEvent(params[0]);
            return null;
        }
    }
}