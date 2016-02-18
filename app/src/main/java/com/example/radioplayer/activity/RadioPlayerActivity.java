package com.example.radioplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.radioplayer.R;
import com.example.radioplayer.fragment.RadioPlayerFragment;

public class RadioPlayerActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_player);

        // set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Radio Player");
        }

        // retrieve the queue position from the intent
        int position = getIntent().getIntExtra(RadioPlayerFragment.BUNDLE_QUEUE_POSITION, 0);

        // add the radio player fragment
        if(getFragmentManager().findFragmentById(R.id.radio_player_fragment_container) == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.radio_player_fragment_container, RadioPlayerFragment.newInstance(position))
                    .commit();
        }
    }
}
