package com.example.myapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapp.R;

/**
 * Created by czc on 2014/8/22.
 */
public class AddDialogFragment extends DialogFragment {

    private AddDialogListener mListener;

    private EditText mEt_content;

    private String title;

    public AddDialogFragment(String title, AddDialogListener mListener) {
        if (title != null) {
            this.title = title;
        } else {
            this.title = "";
        }
        this.mListener = mListener;
    }

    public String getContent() {
        if (mEt_content == null || mEt_content.getText() == null) {
            return null;
        }
        return mEt_content.getText().toString().trim();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_item_dialog, null);
        mEt_content = (EditText) view.findViewById(R.id.content);

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

    public interface AddDialogListener {
        public void onDialogPositiveClick(AddDialogFragment dialog);
        public void onDialogNegativeClick(AddDialogFragment dialog);
    }

}
