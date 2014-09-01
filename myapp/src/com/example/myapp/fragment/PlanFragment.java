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
import android.widget.Toast;

import com.example.myapp.AppUtil;
import com.example.myapp.DetailEventActivity;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.adapter.EventCategoryAdapter;
import com.example.myapp.adapter.EventListAdapter;
import com.example.myapp.adapter.RecordEventListAdapter;
import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.Event;
import com.example.myapp.model.RecordEvent;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by czc on 2014/8/22.
 */
public class PlanFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String COUNT = "100";

    private static final String TAG = "plan_event";

    private Context mContext;

    private TextView mTvAdd;

    private EventCategoryAdapter adapter;

    private ListView mLvEventList;

    private AddDialogFragment dialog;

    private Database mDb;

    private LayoutInflater inflater;

    public PlanFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater(savedInstanceState);
        adapter = new EventCategoryAdapter(inflater);
        TomatoOpenHelper openHelper = ((MainActivity) getActivity()).getOpenHelper();
        mDb = new Database(openHelper);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadTask(mDb, COUNT, adapter, inflater).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plan_fragment_layout, container, false);
        mLvEventList = (ListView) view.findViewById(R.id.event_list);
        mLvEventList.setAdapter(adapter);
        mLvEventList.setOnItemClickListener(this);
        mLvEventList.setOnItemLongClickListener(this);
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
        dialog = new AddDialogFragment(getString(R.string.add_event), new AddDialogFragment.AddDialogListener() {
            @Override
            public void onDialogPositiveClick(AddDialogFragment dialog) {
                Event event;
                String content = dialog.getContent();
                long planTime = dialog.getPlanTime();
                if (content != null && content.length() != 0) {
                    event = new Event(dialog.getContent(), planTime);
                    adapter.addEvent(event);
                    new AddEventTask(mDb).execute(event);
                } else {
                    Toast.makeText(getActivity(), R.string.error_empty_name, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDialogNegativeClick(AddDialogFragment dialog) {
            }
        });
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        //new LoadTask(mDb, COUNT, adapter, inflater).execute();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_confirm)
                .setMessage(R.string.if_delete)
                .setPositiveButton(R.string.confirm,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteEventTask(mDb).execute((Event) adapter.getItem(position));
                        adapter.deleteEvent(position);
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
        long eventId = ((Event) adapter.getItem(position)).getEventID();
        Intent intent = new Intent(getActivity(), DetailEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("event_id", eventId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    static class LoadTask extends AsyncTask<Void, Void, List<Event>> {

        private Database mDb;
        private String count;
        private EventCategoryAdapter mAdapter;
        private LayoutInflater inflater;

        LoadTask(Database mDb, String count, EventCategoryAdapter mAdapter, LayoutInflater inflater) {
            this.mDb = mDb;
            this.count = count;
            this.mAdapter = mAdapter;
            this.inflater = inflater;
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDb.getEvents(count);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
            if (events == null || events.size() == 0)
                return;
            mAdapter.clearCategory();
            events.add(new Event(null, 0));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int start = 0;
            long time = events.get(0).getPlanTime();
            int size = events.size();
            for (int i=1; i<size; i++) {
                Event event = events.get(i);
                if (!AppUtil.isSameDay(time, event.getPlanTime())) {
                    EventListAdapter eventListAdapter = new EventListAdapter(inflater);
                    eventListAdapter.changeEvents(events.subList(start, i));
                    mAdapter.addCategory(AppUtil.timeToString1(time, dateFormat), eventListAdapter);
                    time = event.getPlanTime();
                    start = i;
                }
            }
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