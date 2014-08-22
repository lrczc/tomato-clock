package com.example.myapp;

/**
 * Created by czc on 2014/8/21.
 */
public class Event {
    private int eventID;
    private String eventName;
    private int time;
    private SoundType sound;

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
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
