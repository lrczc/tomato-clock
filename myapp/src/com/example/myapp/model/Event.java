package com.example.myapp.model;

import com.example.myapp.SoundType;

/**
 * Created by czc on 2014/8/21.
 */
public class Event {
    private long eventID;
    private String eventName;
    private int time;
    private SoundType sound;

    public Event() {
        eventID = 0;
        eventName = null;
        time = 0;
        sound = SoundType.dingding;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public SoundType getSound() {
        return sound;
    }

    public void setSound(SoundType sound) {
        this.sound = sound;
    }
}
