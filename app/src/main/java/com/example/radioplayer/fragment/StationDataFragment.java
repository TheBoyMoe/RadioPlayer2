package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.DataModelUpdateEvent;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.StationThreadCompletionEvent;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.network.StationThread;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Headless fragment for maintaining station data between device rotation
 */
public class StationDataFragment extends BaseFragment{

    public static final String STATION_DATA_FRAGMENT_TAG = "station_data_fragment";
    private static final String BUNDLE_CATEGORY_ID = "category_id";
    private List<Station> mStationList = new ArrayList<>();
    private boolean mIsStarted = false;
    private Long mCategoryId;

    public StationDataFragment() {}

    public static StationDataFragment newInstance(Long categoryId) {
        StationDataFragment fragment = new StationDataFragment();
        Bundle args = new Bundle();
        args.putLong(BUNDLE_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }


    // return a copy of the data set
    public ArrayList<Station> getStationData() {
        return new ArrayList<>(mStationList);
    }

    public Station getStationDataItem(int position) {
        return mStationList.get(position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mCategoryId = getArguments().getLong(BUNDLE_CATEGORY_ID);
        if(mCategoryId != null) {
            if(Utils.isClientConnected(getActivity())) {
                if(!mIsStarted) {
                    mIsStarted = true;
                    // TODO pagination
                    new StationThread("StationThread", getActivity(), mCategoryId).start();
                }
            } else {
                Timber.i("Client not connected");
                RadioPlayerApplication.postToBus(new MessageEvent("Not connected, check connection"));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }


    // fetch station list when thread complete
    @Subscribe
    public void getStationList(StationThreadCompletionEvent event) {
        mIsStarted = false; // thread complete
        mStationList = event.getStationList();
        // let the hosting activity know the data model has been updated
        RadioPlayerApplication.postToBus(new DataModelUpdateEvent(DataModelUpdateEvent.STATION_MODEL_DATA));
    }


}
