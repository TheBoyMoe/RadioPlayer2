package com.example.radioplayer.event;

public class RefreshUIEvent extends BaseEvent{

    public static final String REFRESH_CATEGORY_LIST_UI = "refresh_category_ui";
    public static final String REFRESH_STATION_LIST_UI = "refresh_station_ui";
    private String mRefreshEvent;

    public RefreshUIEvent(String refreshEvent) {
        mRefreshEvent = refreshEvent;
    }

    public String getRefreshEvent() {
        return mRefreshEvent;
    }
}
