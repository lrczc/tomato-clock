package com.example.myapp.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
}