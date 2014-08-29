package com.example.myapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.AppUtil;
import com.example.myapp.R;
import com.example.myapp.model.RecordEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by shizhao.czc on 2014/8/27.
 */
public class RecordEventListAdapter extends BaseAdapter {
    LinkedList<RecordEvent> mEventList = new LinkedList<RecordEvent>();
    LayoutInflater mInflater;

    public RecordEventListAdapter(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    public void changeEvents(List<RecordEvent> events) {
        mEventList.clear();
        mEventList.addAll(events);
        notifyDataSetChanged();
    }

    public void addEventsFirst(List<RecordEvent> events) {
        mEventList.addAll(0, events);
        notifyDataSetChanged();
    }

    public void addEventsLast(List<RecordEvent> events) {
        mEventList.addAll(events);
        notifyDataSetChanged();
    }

    public void addEventFirst(RecordEvent event) {
        mEventList.addFirst(event);
        notifyDataSetChanged();
    }

    public void addEventLast(RecordEvent event) {
        mEventList.addLast(event);
        notifyDataSetChanged();
    }

    public void deleteEvent(int position) {
        mEventList.remove(position);
        notifyDataSetChanged();
    }

    public List<RecordEvent> getEventList() {
        return mEventList;
    }

    @Override
    public int getCount() {
        return mEventList.size();
    }

    @Override
    public RecordEvent getItem(int position) {
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

        private ViewHolder(View view) {
            view.setTag(this);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
        }

        public static ViewHolder getFromView(View view) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            } else {
                return new ViewHolder(view);
            }
        }

        public void render(RecordEvent event) {
            title.setText(event.getEventName());
            SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyy-MM-dd");
            String str = "完成日期：" + dateFormat1.format(new Date(event.getCompleteTime()));
            subtitle.setText(str);
        }
    }
}
