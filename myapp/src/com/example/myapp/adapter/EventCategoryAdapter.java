package com.example.myapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.AppUtil;
import com.example.myapp.R;
import com.example.myapp.model.Event;
import com.example.myapp.model.EventCategory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shizhao.czc on 2014/8/29.
 */
public class EventCategoryAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
    private static final String TODAY = "今日";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private List<EventCategory> categories = new ArrayList<EventCategory>();

    LayoutInflater mInflater;

    public EventCategoryAdapter(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    public void clearCategory() {
        categories.clear();
    }

    public void addCategory(String title, EventListAdapter adapter) {
        categories.add(new EventCategory(title, adapter));
        notifyDataSetChanged();
    }

    public void deleteCategoryFirst() {
        categories.remove(0);
    }

    public void deleteEvent(int position){
        for (EventCategory category : categories) {
            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                category.getAdapter().deleteEvent(position-1);
                break;
            }
            position -= size;
        }
        notifyDataSetChanged();
    }

    public void addEvent(Event event) {
        int size = categories.size();
        int pointer = 0;
        for (; pointer<size; pointer++) {
            EventCategory category = categories.get(pointer);
            if (AppUtil.timeToString1(event.getPlanTime(),dateFormat).equals(category.getTitle())) {
                category.getAdapter().addEventLast(event);
                notifyDataSetChanged();
                return;
            } else if (AppUtil.stringToTime(category.getTitle(), dateFormat) > event.getPlanTime()) {
                break;
            }
        }
        EventListAdapter adapter = new EventListAdapter(mInflater);
        adapter.addEventLast(event);
        categories.add(pointer,new EventCategory(AppUtil.timeToString1(event.getPlanTime(),dateFormat), adapter));
        notifyDataSetChanged();
    }

    public List<Event> getAndDeleteOldEvent() {
        List<Event> events = new ArrayList<Event>();
        long today = System.currentTimeMillis();
        int pointer = 0;
        for (; pointer<categories.size(); pointer++) {
            if (!categories.get(pointer).getTitle().equals(AppUtil.timeToString1(today, dateFormat))) {
                events.addAll(categories.get(pointer).getAdapter().getEventList());
            } else break;
        }
        for (int i=0; i<pointer-1; i++) deleteCategoryFirst();
        return events;
    }

    public boolean isTitle(int position) {
        if (getItemViewType(position) == TYPE_SEPARATOR)
            return true;
        return false;
    }

    @Override
    public int getCount() {
        int total = 0;

        for (EventCategory category : categories) {
            total += category.getAdapter().getCount() + 1;
        }

        return total;
    }

    @Override
    public Object getItem(int position) {
        for (EventCategory category : categories) {
            if (position == 0) {
                return category;
            }

            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                return category.getAdapter().getItem(position-1);
            }
            position -= size;
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        for (EventCategory category : categories) {
            if (position == 0) {
                return TYPE_SEPARATOR;
            }

            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                return TYPE_ITEM;
            }
            position -= size;
        }

        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int categoryIndex = 0;

        for (EventCategory category : categories) {
            if (position == 0) {
                return getTitleView(category.getTitle(), categoryIndex,convertView, parent);
            }
            int size = category.getAdapter().getCount()+1;
            if (position < size) {
                return category.getAdapter().getView(position - 1, convertView, parent);
            }
            position -= size;

            categoryIndex++;
        }

        return null;
    }

    private View getTitleView(String caption, int index, View convertView, ViewGroup parent) {
        TextView titleView;

        if (convertView == null) {
            titleView = (TextView)mInflater.inflate(R.layout.title, null);
        } else {
            titleView = (TextView)convertView;
        }
        String today = AppUtil.timeToString1(System.currentTimeMillis(), dateFormat);
        if (caption.equals(today))
            caption = TODAY;
        titleView.setText(caption);
        titleView.setOnClickListener(null);
        titleView.setOnLongClickListener(null);
        return titleView;
    }
}
