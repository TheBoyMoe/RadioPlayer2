package com.example.radioplayer.event;

public class RefreshUIEvent extends BaseEvent{

    private boolean mRefreshUI;

    public RefreshUIEvent(boolean refreshUI) {
        mRefreshUI = refreshUI;
    }

    public boolean isRefreshUI() {
        return mRefreshUI;
    }

}
