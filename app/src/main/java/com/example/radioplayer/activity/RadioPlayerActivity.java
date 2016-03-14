package com.example.radioplayer.activity;

import android.os.Bundle;

import com.example.radioplayer.R;
import com.example.radioplayer.fragment.RadioPlayerFragment;
import com.example.radioplayer.util.Constants;

public class RadioPlayerActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_player);

        setToolbarOnChildActivity(R.id.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Radio Player");
        }

        // retrieve the queue position from the intent
        int position = getIntent().getIntExtra(Constants.KEY_QUEUE_POSITION, 0);

        // add the radio player fragment
        if(getFragmentManager().findFragmentById(R.id.radio_player_fragment_container) == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.radio_player_fragment_container, RadioPlayerFragment.newInstance(position))
                    .commit();
        }
    }
}
