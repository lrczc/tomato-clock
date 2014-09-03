package com.example.myapp;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.animator.Techniques;
import com.example.myapp.animator.YoYo;
import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.fragment.AddDialogFragment;
import com.example.myapp.model.Event;
import com.example.myapp.model.RecordEvent;
import com.nineoldandroids.animation.Animator;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.time.RadialPickerLayout;
import mirko.android.datetimepicker.time.TimePickerDialog;

/**
 * Created by shizhao.czc on 2014/8/26.
 */
public class DetailEventActivity extends FragmentActivity implements IFOnEventFetchListener, DatePickerDialog.OnDateSetListener, Animator.AnimatorListener {

    private static final String TAG = "detail event";

    private TextView mTvEventName;

    private Event mEvent;

    private Spinner mSpSound;

    private TextView mTvPlanTime, mTvRealTime, mTvEndInfo, mTvTime;

    private ImageButton mBtnPlanTime, mBtnEventName, mBtnTime;

    private ArrayAdapter<String> mSoundAdapter;

    private ImageView mBtnStartEvent;

    private TomatoOpenHelper mOpenHelper;

    private YoYo.YoYoString rope;

    private long startTime;

    private boolean starting = false;

    private Database mDb;

    private AddDialogFragment mAddDialog;

    public static final int MSG_UPDATE_EVENT = 0x01;

    public static final int MSG_UPDATE_TIME = 0x02;

    static final int DELAY_TIME = 100;

    private EventHandler mHandler;

    private AlarmManager mAlarmManager;

    PendingIntent pIntent;

    @Override
    public void onEventFetch(Event event) {
        mEvent = event;
        Intent broadcastIntent = new Intent(AlarmReceiver.ALARM_ALERT_ACTION);
        broadcastIntent.putExtra("sound_res", AppUtil.SOUND[mEvent.getSound()]);
        pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadcastIntent, 0);
        mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
        mEvent.setPlanTime(date.getTime());
        new UpdateTask(mDb, mHandler).execute(mEvent);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        animator.start();
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    class EventHandler extends Handler {

        SoftReference<DetailEventActivity> softReference;

        public EventHandler(DetailEventActivity activity) {
            softReference = new SoftReference<DetailEventActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("mm:ss:SSS");
            long totalTime = mEvent.getTime() * 60000;
            switch (msg.what) {
                case MSG_UPDATE_EVENT: {
                    mTvEventName.setText(mEvent.getEventName());
                    if (mEvent.getTime() > 0) {
                        mTvTime.setText(mEvent.getTime() + getString(R.string.minute));
                    } else mTvTime.setText(R.string.not_set_time);
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
                    long remain_time = totalTime - (System.currentTimeMillis() - startTime);
                    if (remain_time > 0) {
                        String realTime = dateFormat2.format(new Date(remain_time));
                        mTvRealTime.setText(realTime);
                        sendEmptyMessageDelayed(MSG_UPDATE_TIME, DELAY_TIME);
                    } else {
                        endClock();
                    }
                    break;
                }
            }
        }
    }

    private void startClock() {
        starting = true;
        mBtnEventName.setEnabled(false);
        mBtnPlanTime.setEnabled(false);
        mBtnTime.setEnabled(false);
        mSpSound.setEnabled(false);
        Calendar calendar = Calendar.getInstance();
        startTime = System.currentTimeMillis();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.MINUTE, mEvent.getTime());
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        //mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pIntent);
        mBtnStartEvent.setImageResource(android.R.drawable.ic_media_pause);
        mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        rope = YoYo.with(Techniques.Shake).duration(1000).playOn(mTvRealTime);
    }

    private void endClock() {
        starting = false;
        new CompleteTask(mDb).execute(mEvent);
        mTvRealTime.setText(R.string.initial_real_time);
        mBtnEventName.setEnabled(false);
        mBtnStartEvent.setEnabled(false);
        mBtnPlanTime.setEnabled(false);
        mBtnTime.setEnabled(false);
        mSpSound.setEnabled(false);
        mTvEndInfo.setVisibility(View.VISIBLE);
        rope = YoYo.with(Techniques.Swing).duration(1000)
                .withListener(this)
                .playOn(mTvRealTime);
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

        mTvEndInfo = (TextView) findViewById(R.id.end_msg);

        mBtnStartEvent = (ImageView) findViewById(R.id.btn_start);
        mBtnStartEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!starting) {
                    if (mEvent.getTime() == 0) {
                        Toast.makeText(DetailEventActivity.this, R.string.not_set_time, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!AppUtil.isSameDay(mEvent.getPlanTime(), System.currentTimeMillis())) {
                        new AlertDialog.Builder(DetailEventActivity.this)
                                .setMessage(R.string.change_to_today)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mEvent.setPlanTime(System.currentTimeMillis());
                                        new UpdateTask(mDb, mHandler).execute(mEvent);
                                        startClock();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create().show();
                    } else {
                        startClock();
                    }
                } else {
                    new AlertDialog.Builder(DetailEventActivity.this)
                            .setMessage(R.string.if_give_up)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBtnEventName.setEnabled(true);
                                    mBtnPlanTime.setEnabled(true);
                                    mBtnTime.setEnabled(true);
                                    mSpSound.setEnabled(true);
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
        mTvEventName = (TextView) findViewById(R.id.tv_event_name);
        mBtnEventName = (ImageButton) findViewById(R.id.btnChangeName);
        mBtnEventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddDialog = new AddDialogFragment(getString(R.string.add_today_event), new AddDialogFragment.AddDialogListener() {
                    @Override
                    public void onDialogPositiveClick(AddDialogFragment dialog) {
                        String content = dialog.getContent();
                        mEvent.setEventName(content);
                        new UpdateTask(mDb, mHandler).execute(mEvent);
                    }

                    @Override
                    public void onDialogNegativeClick(AddDialogFragment dialog) {
                    }
                }, View.INVISIBLE);
                mAddDialog.setContent(mEvent.getEventName());
                mAddDialog.show(getSupportFragmentManager(), TAG);
            }
        });
        mTvRealTime = (TextView) findViewById(R.id.real_time);
        mTvPlanTime = (TextView) findViewById(R.id.plan_time);
        mBtnPlanTime = (ImageButton) findViewById(R.id.btnChangeDate);
        mBtnPlanTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(DetailEventActivity.this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show(getFragmentManager(), TAG);
            }
        });

        mTvTime = (TextView) findViewById(R.id.time);
        mBtnTime = (ImageButton) findViewById(R.id.btnChangeTime);
        mBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog();
                dialog.initialize(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        if (mEvent.getTime() != minute) {
                            mEvent.setTime(minute);
                            new UpdateTask(mDb, mHandler).execute(mEvent);
                        }
                    }
                }, 1, 0, true);
                dialog.show(getFragmentManager(), "tag");
            }
        });

        mSpSound = (Spinner) findViewById(R.id.sound_picker);
        mSoundAdapter = new ArrayAdapter<String>(this, R.layout.sound_spinner_item, AppUtil.SOUND_TITLE);
        mSoundAdapter.setDropDownViewResource(R.layout.sound_spinner_dropdown_item);
        mSpSound.setAdapter(mSoundAdapter);
        mSpSound.setSelection(0, true);
        mSpSound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mEvent.getSound() != position) {
                    mEvent.setSound(position);
                    new UpdateTask(mDb, mHandler).execute(mEvent);
                    mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpSound.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (starting) {
            mAlarmManager.cancel(pIntent);
            Toast.makeText(getApplicationContext(), R.string.mission_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (starting) {
                new AlertDialog.Builder(this)
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
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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

        EventHandler mHandler;

        UpdateTask(Database mDb, EventHandler mHandler) {
            this.mDb = mDb;
            this.mHandler = mHandler;
        }

        @Override
        protected Void doInBackground(Event... params) {
            mDb.updateEvent(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHandler.sendEmptyMessage(MSG_UPDATE_EVENT);
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