package com.example.radioplayer.event;

public class StationThreadCompletionEvent extends BaseEvent{

//    private List<Station> mStationList;
//
//    public StationThreadCompletionEvent(List<Station> stationList) {
//        mStationList = stationList;
//    }
//
//    public List<Station> getStationList() {
//        return mStationList;
//    }

    private boolean mThreadComplete;

    public StationThreadCompletionEvent(boolean threadComplete) {
        mThreadComplete = threadComplete;
    }

    public boolean isThreadComplete() {
        return mThreadComplete;
    }
}
