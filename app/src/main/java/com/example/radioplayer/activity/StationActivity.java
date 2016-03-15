package com.example.radioplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;

import com.example.radioplayer.R;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.fragment.StationFragment;
import com.example.radioplayer.util.Constants;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

public class StationActivity extends BaseActivity{

    public static final String EXTRA_CATEGORY_ID = "category_id_extra";
    public static final String EXTRA_CATEGORY_TITLE = "category_title";

    private StationFragment mStationFragment;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_station);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Long id = getIntent().getLongExtra(EXTRA_CATEGORY_ID, 0);
        String title = getIntent().getStringExtra(EXTRA_CATEGORY_TITLE);

        setToolbarOnChildActivity(R.id.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title + " radio stations");
        }

        // load the UI fragment
        mStationFragment =
                (StationFragment) getFragmentManager().findFragmentById(R.id.station_fragment_container);
        if(mStationFragment == null) {
            mStationFragment = StationFragment.newInstance(id);
            getFragmentManager().beginTransaction()
                    .add(R.id.station_fragment_container, mStationFragment)
                    .commit();
        }
    }

    @Subscribe
    public void getOnClickEvent(OnClickEvent event) {
        if(event.getClickEvent().equals(OnClickEvent.LIST_ITEM_CLICK_EVENT)) {
            int position = event.getPosition();
            Intent intent = new Intent(this, RadioPlayerActivity.class);
            intent.putExtra(Constants.KEY_QUEUE_POSITION, position);
            startActivity(intent);
        }
    }

    // handle message events
    @Subscribe
    public void getMessageEvent(MessageEvent event) {
        Utils.showSnackbar(mCoordinatorLayout, event.getMessage());
    }

}
