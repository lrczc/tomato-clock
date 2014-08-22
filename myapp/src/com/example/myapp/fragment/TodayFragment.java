package com.example.myapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.adapter.EventListAdapter;
import com.example.myapp.model.Event;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by czc on 2014/8/21.
 */
public class TodayFragment extends Fragment {

    private String TAG = "today event";

    private Context mContext;

    private ListView mLvEventList;

    private EventListAdapter mEventAdapter;

    private List<Event> mEventList = new ArrayList<Event>();

    private TextView mTvAdd;

    private AddDialogFragment dialog;

    public TodayFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventAdapter = new EventListAdapter(getLayoutInflater(savedInstanceState));
        mEventAdapter.changeEvents(mEventList);
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
        dialog = new AddDialogFragment(getString(R.string.add_today_event), new AddDialogFragment.AddDialogListener() {
            @Override
            public void onDialogPositiveClick(AddDialogFragment dialog) {
                Event event = new Event();
                if (dialog.getContent() != null) {
                    event.setEventName(dialog.getContent());
                    mEventAdapter.addEventLast(event);
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

}