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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.data.StationDataCache;
import com.example.radioplayer.event.PlaybackServiceEvent;
import com.example.radioplayer.event.QueuePositionEvent;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.service.PlaybackService;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.List;

import timber.log.Timber;

public class PlayerActivity extends AppCompatActivity implements
        View.OnClickListener, ServiceConnection{

    private static final String BUNDLE_STATE = "state";
    public static final String BUNDLE_QUEUE_POSITION = "queue_position";

    private TextView mStationTitle;
    private ImageButton mPlayStopBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private ProgressBar mProgressBar;
    private CoordinatorLayout mCoordinatorLayout;
    private MediaControllerCompat mMediaController;
    private boolean mFirstTimeIn = true;
    private List<Station> mQueue;
    private int mQueuePosition;
    private int mState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mStationTitle = (TextView) findViewById(R.id.station_title);

        // set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Radio Player");
        }

        // setup player controls elements
        mPlayStopBtn = (ImageButton) findViewById(R.id.play_stop_button);
        mPlayStopBtn.setOnClickListener(this);
        mPrevBtn = (ImageButton) findViewById(R.id.previous_button);
        mPrevBtn.setOnClickListener(this);
        mNextBtn = (ImageButton) findViewById(R.id.next_button);
        mNextBtn.setOnClickListener(this);

        // retrieve the queue from the data cache
        mQueue = StationDataCache.getStationDataCache().getStationList();

        if(savedInstanceState != null) {
            mFirstTimeIn = false;

            // restore player state
            mQueuePosition = savedInstanceState.getInt(BUNDLE_QUEUE_POSITION, 0);
            mState = savedInstanceState.getInt(BUNDLE_STATE);

            if(mState == PlaybackStateCompat.STATE_BUFFERING) {
                mPlayStopBtn.setImageResource(R.drawable.action_stop);
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if(mState == PlaybackStateCompat.STATE_PLAYING)
                mPlayStopBtn.setImageResource(R.drawable.action_stop);

        } else {
            mQueuePosition = getIntent().getIntExtra(BUNDLE_QUEUE_POSITION, 0);
        }

        // set the station  and title
        mStationTitle.setText(mQueue.get(mQueuePosition).getName());

        // hide prev/next btns to prevent use if starting from the first or last station
        if(mQueuePosition == 0) {
            mPrevBtn.setVisibility(View.GONE);
        }
        if(mQueuePosition == mQueue.size() - 1) {
            mNextBtn.setVisibility(View.GONE);
        }
        // FIXME service connection leak
        // create the intent used to both start & bind to the service
        Intent intent = new Intent(this, PlaybackService.class);
        this.getApplicationContext().bindService(intent, this, 0);
        Timber.i("Binding Playback Service");
        startService(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // FIXME - the service is not being unbound causing a service connection leak, resulting in the Activity leaking
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mState = mMediaController.getPlaybackState().getState();
        outState.putInt(BUNDLE_STATE, mState);
        outState.putInt(BUNDLE_QUEUE_POSITION, mQueuePosition);
    }

    @Override
    public void onClick(View view) {
        mState = mMediaController.getPlaybackState().getState();
        Timber.i("Current state onClick: %d", mState);
        switch(view.getId()) {

            case R.id.play_stop_button:
                // start playback
                if(mState == PlaybackStateCompat.STATE_NONE || mState == PlaybackStateCompat.STATE_STOPPED) {
                    Timber.i("Clicked play");
                    playFromStationUri();
                // stop playback // FIXME calling stop when buffering causes media player to stop in state 4, error(-38, 0)
                } else if(mState == PlaybackStateCompat.STATE_BUFFERING
                        || mState == PlaybackStateCompat.STATE_PLAYING
                        || mState == PlaybackStateCompat.STATE_CONNECTING){
                    mMediaController.getTransportControls().stop();
                    Timber.i("Clicked stop");
                }
                break;

            case R.id.previous_button:
                if(mState == PlaybackStateCompat.STATE_BUFFERING || mState == PlaybackStateCompat.STATE_PLAYING) {
                    mMediaController.getTransportControls().stop();
                }
                mMediaController.getTransportControls().skipToPrevious();
                mProgressBar.setVisibility(View.VISIBLE);
                break;

            case R.id.next_button:
                if(mState == PlaybackStateCompat.STATE_BUFFERING || mState == PlaybackStateCompat.STATE_PLAYING) {
                    mMediaController.getTransportControls().stop();
                }
                mMediaController.getTransportControls().skipToNext();
                mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(service instanceof PlaybackService.ServiceBinder) {
            try {
                mMediaController = new MediaControllerCompat(PlayerActivity.this,
                        ((PlaybackService.ServiceBinder) service).getService().getMediaSessionToken());

                mMediaController.registerCallback(mMediaControllerCallback);
                mState = mMediaController.getPlaybackState().getState();
                Timber.i("Launching PlayerActivity, connecting to PlaybackService, current mState: %d", mState);

                if(mFirstTimeIn) { // stop restarting every time the device rotates
                    // start playback as soon as player activity launches
                    Timber.i("First time in");
                    if(mState == PlaybackStateCompat.STATE_NONE || mState == PlaybackStateCompat.STATE_STOPPED) {

                        playFromStationUri();

                    } else if(mState == PlaybackStateCompat.STATE_BUFFERING || mState == PlaybackStateCompat.STATE_PLAYING){

                        // if already playing, stop and start the new selected stn
                        mMediaController.getTransportControls().stop();
                        playFromStationUri();
                    }
                }

            } catch (RemoteException e) {
                Timber.e("Error instantiating the media controller: %s", e.getMessage());
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Timber.i("Unexpectedly disconnected from Playback Service");
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

            // IMPL onMetaDataChanged ot update the station title
    };

    // Helper methods ///////////////////////////////////////////////////////

    // show snackbar to the user to display important system events
    @Subscribe
    public void getMessageEvent(PlaybackServiceEvent event) {
        String message = event.getMessage();
        switch (message) {
            case PlaybackServiceEvent.ON_PLAYBACK_ERROR:
            case PlaybackServiceEvent.ON_PLAYBACK_COMPLETION:
            case PlaybackServiceEvent.ON_AUDIO_FOCUS_LOSS:
            case PlaybackServiceEvent.ON_BECOMING_NOISY:
                mPlayStopBtn.setImageResource(R.drawable.action_play);
            case PlaybackServiceEvent.ON_BUFFERING_COMPLETE:
                mProgressBar.setVisibility(View.GONE);
                displayMessage(message);
                break;
            // DEBUG
            default:
                displayMessage(message);
        }
    }

    @Subscribe
    public void getQueuePositionEvent(QueuePositionEvent event) {
        // update queue position and station title
        mQueuePosition = event.getQueuePosition();
        mStationTitle.setText(mQueue.get(mQueuePosition).getName());
        if(mQueuePosition == 0) {
            mPrevBtn.setVisibility(View.GONE);
        } else if(mQueuePosition == mQueue.size() - 1){
            mNextBtn.setVisibility(View.GONE);
        } else {
            mNextBtn.setVisibility(View.VISIBLE);
            mPrevBtn.setVisibility(View.VISIBLE);
        }
        Timber.i("Queue position: %s", mQueuePosition);
    }


    private void displayMessage(String message) {
        Utils.showSnackbar(mCoordinatorLayout, message);
    }


    private void playFromStationUri() {
        Station stn = mQueue.get(mQueuePosition);
        if(stn != null) {

            String name = stn.getName() != null? stn.getName() : "";
            String slug = stn.getSlug() != null? stn.getSlug() : "";
            String country = stn.getCountry() != null? stn.getCountry() : "";
            String imageUrl = stn.getImage().getUrl() != null? stn.getImage().getUrl() : "";
            String thumbUrl = stn.getImage().getThumb().getUrl() != null? stn.getImage().getThumb().getUrl() : "";
            String url = Utils.getStream(stn);

            if(url != null) {
                Timber.i("Url: %s, station: %s", url, stn.getName());
                Uri uri = Uri.parse(url);
                Bundle extras = new Bundle();
                //extras.putParcelable(PlaybackService.EXTRA_STATION, mStation); // PlaybackService can't unmarshall the station object
                extras.putParcelable(PlaybackService.EXTRA_STATION_URI, uri);
                extras.putString(PlaybackService.EXTRA_STATION_NAME, name);
                extras.putString(PlaybackService.EXTRA_STATION_SLUG, slug);
                extras.putString(PlaybackService.EXTRA_STATION_COUNTRY, country);
                extras.putString(PlaybackService.EXTRA_STATION_IMAGE_URL, imageUrl);
                extras.putString(PlaybackService.EXTRA_STATION_THUMB_URL, thumbUrl);
                extras.putInt(PlaybackService.EXTRA_STATION_QUEUE_POSITION, mQueuePosition);

                // playFromUri() works on emulators api 16-19, not on api 21+
                //mMediaController.getTransportControls().playFromUri(uri, extras);
                mMediaController.getTransportControls().playFromSearch("", extras);

                // show the progress bar while buffering the audio stream
                mProgressBar.setVisibility(View.VISIBLE);

            } else {
                displayMessage(PlaybackServiceEvent.ON_NO_STREAM_FOUND);
            }
        }
    }

}
