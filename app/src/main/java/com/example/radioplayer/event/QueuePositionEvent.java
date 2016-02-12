package com.example.radioplayer.event;

public class QueuePositionEvent extends BaseEvent{

    private int mQueuePosition;

    public QueuePositionEvent(int queuePosition) {
        mQueuePosition = queuePosition;
    }

    public int getQueuePosition() {
        return mQueuePosition;
    }
}
