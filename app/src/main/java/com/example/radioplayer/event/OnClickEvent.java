package com.example.radioplayer.event;

public class OnClickEvent extends BaseEvent{

    public static final String GRID_ITEM_CLICK_EVENT = "grid_item_click_event";
    public static final String LIST_ITEM_CLICK_EVENT = "list_item_click_event";
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
