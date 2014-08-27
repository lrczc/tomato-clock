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
import com.example.myapp.adapter.EventListAdapter;
import com.example.myapp.adapter.RecordEventListAdapter;
import com.example.myapp.database.Database;
import com.example.myapp.database.TomatoOpenHelper;
import com.example.myapp.model.Event;
import com.example.myapp.model.RecordEvent;

import java.util.List;

/**
 * Created by czc on 2014/8/22.
 */
public class PlanFragment extends Fragment {

    private Context mContext;

    public PlanFragment(Context context) {
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plan_fragment_layout, container, false);
        return view;
    }

}