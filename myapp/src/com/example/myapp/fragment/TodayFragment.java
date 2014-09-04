package com.example.myapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.AppUtil;
import com.example.myapp.DetailEventActivity;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.adapter.EventListAdapter;
import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.Event;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by czc on 2014/8/21.
 */
public class TodayFragment extends BaseFragment implements ListView.OnItemLongClickListener, ListView.OnItemClickListener {

    private String EVENT_COUNT = "50";

    private String TAG = "today event";

    private TextView mTvBlankInfo;

    private Context mContext;

    private ListView mLvEventList;

    private EventListAdapter mEventAdapter;

    private AddDialogFragment dialog;

    private Database mDb;

    public static final int MSG_FETCH_EVENTS = 0x01;

    public static final int MSG_NOT_FETCH_EVENTS = 0x02;

    TodayHandler mHandler;

    class TodayHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FETCH_EVENTS:
                    mTvBlankInfo.setVisibility(View.INVISIBLE);
                    break;
                case MSG_NOT_FETCH_EVENTS:
                    mTvBlankInfo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public TodayFragment(Context context) {
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TomatoOpenHelper openHelper = ((MainActivity) getActivity()).getOpenHelper();
        mDb = new Database(openHelper);
        mEventAdapter = new EventListAdapter(getLayoutInflater(savedInstanceState));
        mHandler = new TodayHandler();
    }

    @Override
    public void onStart() {
        super.onStart();
        //loadlist();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onResume();
        } else {
            onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadlist();
    }

    private void loadlist() {
        LoadTask mLoadTask = new LoadTask(mDb, EVENT_COUNT, mEventAdapter, mHandler);
        mLoadTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_fragment_layout, container, false);
        mTvBlankInfo = (TextView) view.findViewById(R.id.blank_info);
        mLvEventList = (ListView) view.findViewById(R.id.event_list);
        mLvEventList.setVisibility(View.VISIBLE);
        mLvEventList.setAdapter(mEventAdapter);
        mLvEventList.setOnItemLongClickListener(this);
        mLvEventList.setOnItemClickListener(this);
        return view;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_confirm)
                .setMessage(R.string.if_delete)
                .setPositiveButton(R.string.confirm,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteEventTask(mDb).execute(mEventAdapter.getItem(position));
                        mEventAdapter.deleteEvent(position);
                        if (mEventAdapter.getCount() == 0)
                            mHandler.sendEmptyMessage(MSG_NOT_FETCH_EVENTS);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long eventId = mEventAdapter.getItem(position).getEventID();
        Intent intent = new Intent(getActivity(), DetailEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("event_id", eventId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void actionAddEvent() {
        dialog = new AddDialogFragment(getString(R.string.add_today_event), new AddDialogFragment.AddDialogListener() {
            @Override
            public void onDialogPositiveClick(AddDialogFragment dialog) {
                Event event;
                String content = dialog.getContent();
                long planTime = dialog.getPlanTime();
                if (content != null && content.length() != 0) {
                    event = new Event(dialog.getContent(), planTime);
                    new AddEventTask(mDb).execute(event);
                    Toast.makeText(getActivity(), R.string.add_success, Toast.LENGTH_SHORT).show();
                    if (AppUtil.isSameDay(planTime, System.currentTimeMillis())) {
                        mEventAdapter.addEventLast(event);
                        mHandler.sendEmptyMessage(MSG_FETCH_EVENTS);
                    } else ((MainActivity) getActivity()).switchTo(MainActivity.PLAN);
                } else {
                    Toast.makeText(getActivity(), R.string.error_empty_name, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onDialogNegativeClick(AddDialogFragment dialog) {
            }
        }, View.VISIBLE);
        dialog.show(getFragmentManager(), TAG);
    }

    static class LoadTask extends AsyncTask<Void, Void, List<Event>> {

        private Database mDb;
        private String count;
        private EventListAdapter mEventAdapter;
        private TodayHandler mHandler;

        LoadTask(Database mDb, String count, EventListAdapter mEventAdapter, TodayHandler mHandler) {
            this.mDb = mDb;
            this.count = count;
            this.mEventAdapter = mEventAdapter;
            this.mHandler = mHandler;
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDb.getTodayEvents(count);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
            if (events.size() == 0) {
                mHandler.sendEmptyMessage(MSG_NOT_FETCH_EVENTS);
            } else mHandler.sendEmptyMessage(MSG_FETCH_EVENTS);
            mEventAdapter.changeEvents(events);
        }
    }

    static class AddEventTask extends AsyncTask<Event, Void, Void> {
        private Database mDb;

        AddEventTask(Database mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Void doInBackground(Event... params) {
            int count = params.length;
            for (int i=0; i<count; i++) {
                params[i].setEventID(mDb.insertEvent(params[i]));
            }
            return null;
        }
    }

    static class DeleteEventTask extends AsyncTask<Event, Void, Void> {
        private Database mDb;

        DeleteEventTask(Database mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Void doInBackground(Event... params) {
            int count = params.length;
            for (int i=0; i<count; i++) {
                mDb.deleteEvent(params[i]);
            }
            return null;
        }
    }

}