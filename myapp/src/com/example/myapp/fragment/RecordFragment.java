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

import com.example.myapp.DetailRecordEventActivity;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.adapter.RecordEventListAdapter;
import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.RecordEvent;

import java.util.List;

/**
 * Created by czc on 2014/8/22.
 */
public class RecordFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private Context mContext;

    private ListView mLvRecordEventList;

    private RecordEventListAdapter mEventAdapter;

    private Database mDb;

    public RecordFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TomatoOpenHelper openHelper = ((MainActivity) getActivity()).getOpenHelper();
        mDb = new Database(openHelper);
        mEventAdapter = new RecordEventListAdapter(getLayoutInflater(savedInstanceState));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        new LoadTask(mDb, mEventAdapter).execute();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment_layout, container, false);
        mLvRecordEventList = (ListView) view.findViewById(R.id.event_list);
        mLvRecordEventList.setVisibility(View.VISIBLE);
        mLvRecordEventList.setAdapter(mEventAdapter);
        mLvRecordEventList.setOnItemLongClickListener(this);
        mLvRecordEventList.setOnItemClickListener(this);
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
        Intent intent = new Intent(getActivity(), DetailRecordEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("event_id", eventId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    static class LoadTask extends AsyncTask<Void, Void, List<RecordEvent>> {

        private Database mDb;
        private RecordEventListAdapter mEventAdapter;

        LoadTask(Database mDb, RecordEventListAdapter mEventAdapter) {
            this.mDb = mDb;
            this.mEventAdapter = mEventAdapter;
        }

        @Override
        protected List<RecordEvent> doInBackground(Void... params) {
            return mDb.getRecordEvents();
        }

        @Override
        protected void onPostExecute(List<RecordEvent> events) {
            super.onPostExecute(events);
            mEventAdapter.changeEvents(events);
        }
    }

    static class DeleteEventTask extends AsyncTask<RecordEvent, Void, Void> {
        private Database mDb;

        DeleteEventTask(Database mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Void doInBackground(RecordEvent... params) {
            int count = params.length;
            for (int i=0; i<count; i++) {
                mDb.deleteRecordEvent(params[i]);
            }
            return null;
        }
    }
}