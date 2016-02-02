package com.example.radioplayer.event;

import com.example.radioplayer.model.Station;

import java.util.List;

public class StationThreadCompletionEvent extends BaseEvent{

    private List<Station> mStationList;

    public StationThreadCompletionEvent(List<Station> stationList) {
        mStationList = stationList;
    }

    public List<Station> getStationList() {
        return mStationList;
    }
}
