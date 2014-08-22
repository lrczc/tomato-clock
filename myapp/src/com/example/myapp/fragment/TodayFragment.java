package com.example.myapp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.AddItemDialog;
import com.example.myapp.R;

/**
 * Created by czc on 2014/8/21.
 */
public class TodayFragment extends Fragment {

    Context mContext;


    public TodayFragment(Context context) {
        mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_fragment_layout, container, false);

        return view;
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

                        }
                    }
                })
                .create()
                .show();
    }
}