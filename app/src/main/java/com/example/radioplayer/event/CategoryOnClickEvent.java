package com.example.radioplayer.event;

public class CategoryOnClickEvent extends BaseEvent{

    private int mPosition;

    public CategoryOnClickEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

}
