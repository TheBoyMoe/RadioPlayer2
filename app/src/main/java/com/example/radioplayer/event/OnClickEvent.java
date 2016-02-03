package com.example.radioplayer.event;

public class OnClickEvent extends BaseEvent{

    public static final String CATEGORY_ON_CLICK_EVENT = "categoryOnClickEvent";
    public static final String STATION_ON_CLICK_EVENT = "stationOnClickEvent";
    private String mClickEvent;
    private int mPosition;

    public OnClickEvent(String clickEvent, int position) {
        mClickEvent = clickEvent;
        mPosition = position;
    }

    public String getClickEvent() {
        return mClickEvent;
    }

    public int getPosition() {
        return mPosition;
    }
}
