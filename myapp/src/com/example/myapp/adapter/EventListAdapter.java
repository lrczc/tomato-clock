package com.example.myapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.model.Event;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by czc on 2014/8/22.
 */
public class EventListAdapter extends BaseAdapter {
    LinkedList<Event> mEventList = new LinkedList<Event>();
    LayoutInflater mInflater;

    public EventListAdapter(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    public void changeEvents(List<Event> events) {
        mEventList.clear();
        mEventList.addAll(events);
        notifyDataSetChanged();
    }

    public void addEventsFirst(List<Event> events) {
        //mEventList.addFirst();
    }

    public void addEventsLast(List<Event> events) {

    }

    public void addEventFirst(Event event) {

    }

    public void addEventLast(Event event) {

    }

    public List<Event> getEventList() {
        return null;
    }

    @Override
    public int getCount() {
        return mEventList.size();
    }

    @Override
    public Event getItem(int position) {
        return mEventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEventList.get(position).getEventID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.event_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = ViewHolder.getFromView(convertView);
        }
        viewHolder.render(getItem(position));
        return convertView;
    }

    private static class ViewHolder {
        private TextView title;
        private TextView subtitle;

        private static String MINUTE = "分钟";
        private static String NO_TIME = "未设置时间";

        private ViewHolder(View view) {
            view.setTag(this);
        }

        public static ViewHolder getFromView(View view) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            } else {
                return new ViewHolder(view);
            }
        }

        public void render(Event event) {
            title.setText(event.getEventName());
            if (event.getTime() != 0) {
                subtitle.setText(event.getTime()+MINUTE);
            } else {
                subtitle.setText(NO_TIME);
            }
        }
    }
}
