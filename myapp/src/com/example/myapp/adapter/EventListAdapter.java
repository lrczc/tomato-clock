package com.example.myapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.AppUtil;
import com.example.myapp.R;
import com.example.myapp.model.Event;
import com.example.myapp.widget.LetterImageView;

import java.util.ArrayList;
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

    public void addEventLast(Event event) {
        mEventList.addLast(event);
        notifyDataSetChanged();
    }

    public void deleteEvent(int position) {
        mEventList.remove(position);
        notifyDataSetChanged();
    }

    public List<Event> getEventList() {
        return mEventList;
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
        viewHolder.render(getItem(position), position);
        return convertView;
    }

    private static class ViewHolder {
        private TextView title;
        private TextView subtitle;
        private LetterImageView mLetterIV;

        private static String NO_TIME = "未设置时间";
        private static String MINUTE = "分钟";

        private ViewHolder(View view) {
            view.setTag(this);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            mLetterIV = (LetterImageView) view.findViewById(R.id.iv_avatar);
        }

        public static ViewHolder getFromView(View view) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            } else {
                return new ViewHolder(view);
            }
        }

        public void render(Event event, int position) {
            //mLetterIV.setOval(true);
            mLetterIV.setText(String.valueOf(event.getTime()));
            mLetterIV.setPosition(position);
            title.setText(event.getEventName());
            if (event.getTime() != 0) {
                subtitle.setText(event.getTime()+MINUTE);
            } else {
                subtitle.setText(NO_TIME);
            }
        }
    }
}
