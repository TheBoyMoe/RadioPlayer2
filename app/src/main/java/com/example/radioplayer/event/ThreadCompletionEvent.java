package com.example.radioplayer.event;

import java.util.List;

public class ThreadCompletionEvent extends BaseEvent{

    // FIXME results in casting error(casting station to category) on device rotation when returning to the CategoryFragment
    private List<?> mList;

    public ThreadCompletionEvent(List<?> list) {
        mList = list;
    }

    public List<?> getList() {
        return mList;
    }
}
