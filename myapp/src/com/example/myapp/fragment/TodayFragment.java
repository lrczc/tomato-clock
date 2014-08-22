package com.example.myapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapp.AddItemDialog;
import com.example.myapp.R;
import com.example.myapp.adapter.EventListAdapter;
import com.example.myapp.model.Event;

import java.lang.ref.SoftReference;

/**
 * Created by czc on 2014/8/21.
 */
public class TodayFragment extends Fragment {

    private Context mContext;

    private ListView mLvEventList;

    private EventListAdapter mEventAdapter;

    private TextView mTvAdd;

    public TodayFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventAdapter = new EventListAdapter(getLayoutInflater(savedInstanceState));
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

    public void loadEvents() {

    }


    private void addItem() {
        new AddItemDialog.Builder(mContext)
                .setTitle("aaa")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddItemDialog addDialog = (AddItemDialog) dialog;
                        String content = addDialog.getContent();
                        if (content != null && content != "") {
                            Event event = new Event();
                            event.setEventName(content);
                            mEventAdapter.addEventLast(event);
                        } else {
                            new AlertDialog.Builder(mContext)
                                    .setMessage("添加失败")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                })
                .create()
                .show();
    }
}