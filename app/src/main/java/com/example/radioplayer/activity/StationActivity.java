package com.example.radioplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.fragment.RadioPlayerFragment;
import com.example.radioplayer.fragment.StationFragment;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

public class StationActivity extends AppCompatActivity{

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Subscribe
    public void getOnClickEvent(OnClickEvent event) {
        if(event.getClickEvent().equals(OnClickEvent.STATION_ON_CLICK_EVENT)) {
            int position = event.getPosition();
            Intent intent = new Intent(this, RadioPlayerActivity.class);
            intent.putExtra(RadioPlayerFragment.BUNDLE_QUEUE_POSITION, position);
            startActivity(intent);
        }
    }

    // handle message events
    @Subscribe
    public void getMessageEvent(MessageEvent event) {
        Utils.showSnackbar(mCoordinatorLayout, event.getMessage());
    }

}
