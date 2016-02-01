package com.example.radioplayer.event;

public class DataModelUpdateEvent extends BaseEvent{

    public static final String CATEGORY_MODEL_DATA = "category_model_data";
    public static final String STATION_MODEL_DATA = "station_model_data";
    private boolean mDataModelUpdate;
    private String mDataModel;

    public DataModelUpdateEvent(String dataModel, boolean dataModelUpdate) {
        mDataModelUpdate = dataModelUpdate;
        mDataModel = dataModel;
    }

    public boolean isDataModelUpdate() {
        return mDataModelUpdate;
    }

    public String getDataModel() {
        return mDataModel;
    }
}
