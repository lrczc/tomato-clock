package com.example.myapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapp.AppUtil;
import com.example.myapp.DetailEventActivity;
import com.example.myapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mirko.android.datetimepicker.date.DatePickerDialog;

/**
 * Created by czc on 2014/8/22.
 */
public class AddDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "add dialog";

    private AddDialogListener mListener;

    private EditText mEt_content;

    private String content;

    private String title;

    private TextView mTvPlanTime;

    private long mPlanTime;

    private int planTimeVisable;

    public AddDialogFragment(String title, AddDialogListener mListener, int planTimeVisable) {
        if (title != null) {
            this.title = title;
        } else {
            this.title = "";
        }
        this.mListener = mListener;
        this.planTimeVisable = planTimeVisable;
    }

    public void setContent(int resid) {
        content = getString(resid);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        if (mEt_content == null || mEt_content.getText() == null) {
            return null;
        }
        return mEt_content.getText().toString().trim();
    }

    public long getPlanTime() {
        return mPlanTime;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_item_dialog, null);
        mPlanTime = System.currentTimeMillis();
        mEt_content = (EditText) view.findViewById(R.id.content);
        mEt_content.setText(content);
        mTvPlanTime = (TextView) view.findViewById(R.id.plan_time);
        mTvPlanTime.setText(R.string.today);
        view.findViewById(R.id.btnChangeDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddDialogFragment.this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show(getActivity().getFragmentManager(), TAG);
            }
        });
        view.findViewById(R.id.plan_time_layout).setVisibility(planTimeVisable);
        builder.setView(view)
                .setTitle(title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(AddDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(AddDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Date date = new Date(year-1900, monthOfYear, dayOfMonth);
        mPlanTime = date.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (AppUtil.isSameDay(mPlanTime, System.currentTimeMillis())) {
            mTvPlanTime.setText(R.string.today);
        } else {
            mTvPlanTime.setText(AppUtil.timeToString1(mPlanTime, dateFormat));
        }
    }


    public interface AddDialogListener {
        public void onDialogPositiveClick(AddDialogFragment dialog);
        public void onDialogNegativeClick(AddDialogFragment dialog);
    }

}
