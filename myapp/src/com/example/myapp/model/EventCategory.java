package com.example.myapp.model;

import com.example.myapp.adapter.EventListAdapter;

import java.util.Date;

/**
 * Created by shizhao.czc on 2014/8/29.
 */
public class EventCategory {
    private String mTitle;
    private EventListAdapter mAdapter;

    public EventCategory(String mTitle, EventListAdapter mAdapter) {
        this.mTitle = mTitle;
        this.mAdapter = mAdapter;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public EventListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(EventListAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }
}
