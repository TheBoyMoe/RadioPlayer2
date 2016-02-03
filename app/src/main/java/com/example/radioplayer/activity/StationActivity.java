package com.example.radioplayer.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.DataModelUpdateEvent;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.event.RefreshUIEvent;
import com.example.radioplayer.fragment.StationDataFragment;
import com.example.radioplayer.fragment.StationFragment;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

public class StationActivity extends AppCompatActivity{

    public static final String EXTRA_CATEGORY_ID = "category_id_extra";
    private StationDataFragment mStationDataFragment;
    private StationFragment mStationFragment;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_station);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Long id = getIntent().getLongExtra(EXTRA_CATEGORY_ID, 0);

        // load the data fragment
        mStationDataFragment =
                (StationDataFragment) getFragmentManager().findFragmentByTag(StationDataFragment.STATION_DATA_FRAGMENT_TAG);
        if(mStationDataFragment == null) {
            mStationDataFragment = StationDataFragment.newInstance(id);
            getFragmentManager().beginTransaction()
                    .add(mStationDataFragment, StationDataFragment.STATION_DATA_FRAGMENT_TAG)
                    .commit();
        }

        // load the UI fragment
        mStationFragment =
                (StationFragment) getFragmentManager().findFragmentById(R.id.station_fragment_container);
        if(mStationFragment == null) {
            mStationFragment = StationFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.station_fragment_container, mStationFragment)
                    .commit();
        }

        if(mStationFragment != null && mStationDataFragment != null) {
            mStationFragment.setStationData(mStationDataFragment.getStationData());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        RadioPlayerApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RadioPlayerApplication.getInstance().getBus().unregister(this);
    }

    // handle data model update events
    @Subscribe
    public void dataModelUpdate(DataModelUpdateEvent event) {
        String update = event.getDataModel();
        if(update.equals(DataModelUpdateEvent.STATION_MODEL_DATA)) {
            // update the station list and post refresh event
            if(mStationFragment != null && mStationDataFragment != null) {
                mStationFragment.setStationData(mStationDataFragment.getStationData());
                RadioPlayerApplication.postToBus(new RefreshUIEvent(RefreshUIEvent.REFRESH_STATION_LIST_UI));
            }
        }
    }

    // handle station list on Click events
    @Subscribe
    public void getOnClickEvent(OnClickEvent event) {
        if(event.getClickEvent().equals(OnClickEvent.STATION_ON_CLICK_EVENT)) {
            if(mStationDataFragment != null) {
                Station stn = mStationDataFragment.getStationDataItem(event.getPosition());
                Utils.showSnackbar(mCoordinatorLayout, "Clicked on " + stn.getName());
            }
        }
    }


    // handle message events
    @Subscribe
    public void getMessageEvent(MessageEvent event) {
        // FIXME event is posted before the class has had a chance to register
        Utils.showSnackbar(mCoordinatorLayout, event.getMessage());
    }




}
