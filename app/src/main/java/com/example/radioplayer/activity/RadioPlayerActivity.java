package com.example.radioplayer.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.radioplayer.R;
import com.example.radioplayer.fragment.RadioPlayerFragment;
import com.example.radioplayer.util.Constants;

public class RadioPlayerActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the toolbar, remove the title, enable and set the nav icon
        setContentView(R.layout.activity_radio_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.action_back_player));
        }

        // remove the appbar's elevation
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.setElevation(0.0f);
        }

        // retrieve the queue position from the intent
        int position = getIntent().getIntExtra(Constants.KEY_QUEUE_POSITION, 0);

        // add the radio player fragment
        if(getSupportFragmentManager().findFragmentById(R.id.radio_player_fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.radio_player_fragment_container, RadioPlayerFragment.newInstance(position))
                    .commit();
        }
    }



}
