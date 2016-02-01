package com.example.radioplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.radioplayer.R;
import com.example.radioplayer.fragment.StationFragment;

public class StationActivity extends AppCompatActivity{

    public static final String CATEGORY_ID_EXTRA = "category_id_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_station);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Long id = getIntent().getLongExtra(CATEGORY_ID_EXTRA, 0);

        StationFragment stationFragment =
                (StationFragment) getFragmentManager().findFragmentById(R.id.station_fragment_container);
        if(stationFragment == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.station_fragment_container, StationFragment.newInstance(id))
                    .commit();
        }

    }
}
