package com.example.myapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by czc on 2014/8/21.
 */
public class TodayActivity extends ActionBarActivity {



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.today_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void addItem() {
        new AddItemDialog.Builder(this)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                addItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}