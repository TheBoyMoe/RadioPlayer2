package com.example.radioplayer.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.radioplayer.R;
import com.example.radioplayer.model.Station;

import timber.log.Timber;

public class PlayerActivity extends AppCompatActivity {

    public static final String EXTRA_STATION = "station";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Station stn = getIntent().getParcelableExtra(EXTRA_STATION);
        Timber.i("Radio station %s", stn);

    }

}
