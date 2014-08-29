package com.example.myapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapp.DetailEventActivity;
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
public class TodayFragment extends Fragment implements ListView.OnItemLongClickListener, ListView.OnItemClickListener {

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
    }

    @Override
    public void onStart() {
        super.onStart();
        loadlist();
    }

    private void loadlist() {
        LoadTask mLoadTask = new LoadTask(mDb, EVENT_COUNT, mEventAdapter);
        mLoadTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_fragment_layout, container, false);
        mLvEventList = (ListView) view.findViewById(R.id.event_list);
        mLvEventList.setVisibility(View.VISIBLE);
        mLvEventList.setAdapter(mEventAdapter);
        mLvEventList.setOnItemLongClickListener(this);
        mLvEventList.setOnItemClickListener(this);
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
                Event event;
                if (dialog.getContent() != null) {
                    event = new Event(dialog.getContent(), System.currentTimeMillis());
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

    static class LoadTask extends AsyncTask<Void, Void, List<Event>> {

        private Database mDb;
        private String count;
        private EventListAdapter mEventAdapter;

        LoadTask(Database mDb, String count, EventListAdapter mEventAdapter) {
            this.mDb = mDb;
            this.count = count;
            this.mEventAdapter = mEventAdapter;
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDb.getTodayEvents(count);
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