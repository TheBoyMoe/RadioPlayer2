package com.example.radioplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.PlaybackServiceEvent;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.model.Stream;
import com.example.radioplayer.service.PlaybackService;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import timber.log.Timber;

public class PlayerActivity extends AppCompatActivity implements
        View.OnClickListener, ServiceConnection{

    //public static final String EXTRA_STATION = "station";
    private static final String BUNDLE_STATE = "state";
    public static final String EXTRA_QUEUE_POSITION = "queue_position";
    public static final String EXTRA_STATION_QUEUE = "station_queue"; // station list

    private TextView mStationtitle;
    private ImageButton mPlayStopBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private ProgressBar mProgressBar;
    private CoordinatorLayout mCoordinatorLayout;
    private MediaControllerCompat mMediaController;
    private Station mStation;
    private boolean mFirstTimeIn = true;
    private ArrayList<Station> mQueue;
    private int mQueueItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mStationtitle = (TextView) findViewById(R.id.station_title);

        // set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup player controls elements
        mPlayStopBtn = (ImageButton) findViewById(R.id.play_stop_button);
        mPlayStopBtn.setOnClickListener(this);
        mPrevBtn = (ImageButton) findViewById(R.id.previous_button);
        mPrevBtn.setOnClickListener(this);
        mNextBtn = (ImageButton) findViewById(R.id.next_button);
        mNextBtn.setOnClickListener(this);


        //Station stn = getIntent().getParcelableExtra(EXTRA_STATION);
        mQueueItem = getIntent().getIntExtra(EXTRA_QUEUE_POSITION, 0);
        mQueue = getIntent().getParcelableArrayListExtra(EXTRA_STATION_QUEUE);
        mStation = mQueue.get(mQueueItem);
        mStationtitle.setText(mStation.getName());

        // create the intent used to both start & bind to the service
        Intent intent = new Intent(this, PlaybackService.class);
        this.getApplicationContext().bindService(intent, this, 0);
        Timber.i("Binding Playback Service");
        startService(intent);


        // set the progress bar and play stop btn on device rotation
        if(savedInstanceState != null) {
            mFirstTimeIn = false;
            int state = savedInstanceState.getInt(BUNDLE_STATE);
            if(state == PlaybackStateCompat.STATE_BUFFERING) {
                mPlayStopBtn.setImageResource(R.drawable.action_stop);
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if(state == PlaybackStateCompat.STATE_PLAYING)
                mPlayStopBtn.setImageResource(R.drawable.action_stop);
        }

        // hide prev/next btns to prevent use if starting from the first or last station
        if(mQueueItem == 0) {
            mPrevBtn.setVisibility(View.GONE);
        }
        if(mQueueItem == mQueue.size() - 1) {
            mNextBtn.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int state = mMediaController.getPlaybackState().getState();
        outState.putInt(BUNDLE_STATE, state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getApplicationContext().unbindService(this);
        Timber.i("Unbinding Playback Service");
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
    public void getPlaybackServiceEvents(PlaybackServiceEvent event) {
        switch (event.getMessage()) {
            case PlaybackServiceEvent.ON_BUFFERING_COMPLETE:
                mProgressBar.setVisibility(View.GONE);
                break;
            case PlaybackServiceEvent.ON_PLAYBACK_ERROR:
                Utils.showSnackbar(mCoordinatorLayout, "An error has occurred, playback terminated");
                mProgressBar.setVisibility(View.GONE);
                break;
            case PlaybackServiceEvent.ON_AUDIO_FOCUS_LOSS:
                // another app has audio focus - display message to user
                Utils.showSnackbar(mCoordinatorLayout, "Another app has gained audio focus, playback terminated");
                break;
            case PlaybackServiceEvent.ON_PLAYBACK_COMPLETION:
                Utils.showSnackbar(mCoordinatorLayout, "Playback has come to an end");
                mProgressBar.setVisibility(View.GONE);
                break;
            case PlaybackServiceEvent.ON_STOP:
                // ??
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int state = mMediaController.getPlaybackState().getState();
        switch(view.getId()) {

            case R.id.play_stop_button:
                if(state == PlaybackStateCompat.STATE_NONE || state == PlaybackStateCompat.STATE_STOPPED) {
                    // start playback
                    playFromStationUri();
                } else if(state == PlaybackStateCompat.STATE_BUFFERING || state == PlaybackStateCompat.STATE_PLAYING){
                    // stop playback
                    mMediaController.getTransportControls().stop();
                }
                break;

            case R.id.previous_button:
                if(state == PlaybackStateCompat.STATE_BUFFERING || state == PlaybackStateCompat.STATE_PLAYING) {
                    mMediaController.getTransportControls().stop();
                }
                --mQueueItem;
                updatePlayer();
                break;

            case R.id.next_button:
                if(state == PlaybackStateCompat.STATE_BUFFERING || state == PlaybackStateCompat.STATE_PLAYING) {
                    mMediaController.getTransportControls().stop();
                }
                ++mQueueItem;
                updatePlayer();
                break;
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(service instanceof PlaybackService.ServiceBinder) {
            try {
                mMediaController = new MediaControllerCompat(this,
                        ((PlaybackService.ServiceBinder) service).getService().getMediaSessionToken());

                if(mFirstTimeIn) { // stop restarting every time the device rotates
                    // start playback as soon as player activity launches
                    int state = mMediaController.getPlaybackState().getState();
                    if(state == PlaybackStateCompat.STATE_NONE || state == PlaybackStateCompat.STATE_STOPPED) {

                        playFromStationUri();

                    } else if(state == PlaybackStateCompat.STATE_BUFFERING || state == PlaybackStateCompat.STATE_PLAYING){

                        // if already playing, stop and start new stn selected
                        mMediaController.getTransportControls().stop();
                        playFromStationUri();
                    }
                }

            } catch (RemoteException e) {
                Timber.e("Error instantiating the media controller: %s", e.getMessage());
            }
            mMediaController.registerCallback(mMediaControllerCallback);
            Timber.i("Connected to Playback Service");
        }
    }


    // TODO re-enable prev/next btns
    private void updatePlayer() {
        // ensure index not out of bounds
        if(mQueueItem >=0 && mQueueItem < mQueue.size()) {
            // TODO re-enable
            mPrevBtn.setVisibility(View.VISIBLE);
            mNextBtn.setVisibility(View.VISIBLE);
            mStation = mQueue.get(mQueueItem);
            mStationtitle.setText(mStation.getName());
            playFromStationUri();

            // update prev/next btns if req'd
            if(mQueueItem == 0) {
                mPrevBtn.setVisibility(View.GONE);
            }
            if(mQueueItem == mQueue.size() - 1) {
                mNextBtn.setVisibility(View.GONE);
            }
        }
//        else {
//            // TODO disable
//
//            // disable the appropriate button
//            Utils.showSnackbar(mCoordinatorLayout, "reached the end of the line");
//        }
    }


    private void playFromStationUri() {
        if(mStation != null) {
            Uri uri = Uri.parse(getStream(mStation));
            if (uri != null) {
                Bundle extras = new Bundle();
                extras.putParcelable(PlaybackService.EXTRA_STATION, mStation);
                mMediaController.getTransportControls().playFromUri(uri, extras);
                Timber.i("queue position: %d", mQueueItem);

                // show the progress bar while buffering the audio stream
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                Utils.showSnackbar(mCoordinatorLayout, "No stream found, try a different station");
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Timber.i("Disconnected from Playback Service");
    }


    private MediaControllerCompat.Callback mMediaControllerCallback =
        new MediaControllerCompat.Callback() {

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                switch (state.getState()) {
                    case PlaybackStateCompat.STATE_NONE:
                    case PlaybackStateCompat.STATE_STOPPED:
                        mPlayStopBtn.setImageResource(R.drawable.action_play);
                        break;
                    case PlaybackStateCompat.STATE_BUFFERING:
                    case PlaybackStateCompat.STATE_PLAYING:
                        mPlayStopBtn.setImageResource(R.drawable.action_stop);
                        break;
                }
            }
    };


    // retrieve the stream url from the station object with a status of 1 or greater
    private String getStream(Station stn) {

        String url = null;
        ArrayList<Stream> streams = (ArrayList<Stream>) stn.getStreams();
        int status;
        for (Stream stream : streams) {
            status = stream.getStatus();
            if(status >= 0) {
                url = stream.getStream();
                if(url != null && !url.isEmpty())
                    break;
            }
        }
        return url;
    }

}
