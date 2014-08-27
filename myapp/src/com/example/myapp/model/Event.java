package com.example.myapp.model;


import java.util.Date;

/**
 * Created by czc on 2014/8/21.
 */
public class Event extends BaseEvent{

    public Event() {
        super();
    }

    public Event(String eventName, long planTime) {
        this.eventName = eventName;
        this.planTime = planTime;
    }

}
