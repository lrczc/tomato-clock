package com.example.myapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.adapter.EventListAdapter;
import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.Event;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by czc on 2014/8/21.
 */
public class TodayFragment extends Fragment {

    private String EVENT_COUNT = "50";

    private String TAG = "today event";

    private Context mContext;

    private ListView mLvEventList;

    private EventListAdapter mEventAdapter;

    private List<Event> mEventList = new ArrayList<Event>();

    private TextView mTvAdd;

    private AddDialogFragment dialog;

    private Database mDb;

    public TodayFragment(Context context) {
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TomatoOpenHelper openHelper = ((MainActivity) getActivity()).getOpenHelper();
        mDb = new Database(openHelper);
        mEventAdapter = new EventListAdapter(getLayoutInflater(savedInstanceState));
        mEventAdapter.changeEvents(mEventList);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadlist();
    }

    private void loadlist() {
        new LoadTask(mDb, EVENT_COUNT, mEventAdapter).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_fragment_layout, container, false);
        mLvEventList = (ListView) view.findViewById(R.id.event_list);
        mLvEventList.setVisibility(View.VISIBLE);
        mLvEventList.setAdapter(mEventAdapter);
        mTvAdd = (TextView) view.findViewById(R.id.add_item);
        mTvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        return view;
    }


    private void addItem() {
        dialog = new AddDialogFragment(getString(R.string.add_today_event), new AddDialogFragment.AddDialogListener() {
            @Override
            public void onDialogPositiveClick(AddDialogFragment dialog) {
                Event event = new Event();
                if (dialog.getContent() != null) {
                    event.setEventName(dialog.getContent());
                    mEventAdapter.addEventLast(event);
                    new AddEventTask(mDb).execute(event);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.error_info))
                            .setMessage(getString(R.string.add_failed))
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                }
            }

            @Override
            public void onDialogNegativeClick(AddDialogFragment dialog) {
            }
        });
        dialog.show(getFragmentManager(), TAG);
    }

    static class LoadTask extends AsyncTask<Void, Void, List<Event>> {

        private Database mDb;
        private String count;
        private EventListAdapter mEventAdapter;

        LoadTask(Database mDb, String count, EventListAdapter mEventAdapter) {
            this.mDb = mDb;
            this.count = count;
            this.mEventAdapter = mEventAdapter;
        }

        public Database getmDb() {
            return mDb;
        }

        public void setmDb(Database mDb) {
            this.mDb = mDb;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDb.getTodayEvent(count);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
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
                mDb.insertEvent(params[i]);
            }
            return null;
        }


    }
}