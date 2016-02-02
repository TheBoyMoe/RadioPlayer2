package com.example.radioplayer.event;

public class DataModelUpdateEvent extends BaseEvent{

    public static final String CATEGORY_MODEL_DATA = "category_model_data";
    public static final String STATION_MODEL_DATA = "station_model_data";
    private String mDataModel;

    public DataModelUpdateEvent(String dataModel) {
        mDataModel = dataModel;
    }

    public String getDataModel() {
        return mDataModel;
    }
}
