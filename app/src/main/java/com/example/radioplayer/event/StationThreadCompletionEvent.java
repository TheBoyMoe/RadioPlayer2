package com.example.radioplayer.event;

public class StationThreadCompletionEvent extends BaseEvent{

    private boolean mThreadComplete;
    private boolean mDownloadComplete;

    public StationThreadCompletionEvent(boolean threadComplete, boolean downloadComplete) {
        mThreadComplete = threadComplete;
        mDownloadComplete = downloadComplete;
    }

    public boolean isThreadComplete() {
        return mThreadComplete;
    }

    public boolean isDownloadComplete() {
        return mDownloadComplete;
    }

}
