package com.example.radioplayer.fragment;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.radioplayer.R;
import com.example.radioplayer.data.StationDataCache;
import com.example.radioplayer.event.PlaybackServiceEvent;
import com.example.radioplayer.event.QueuePositionEvent;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.service.PlaybackService;
import com.example.radioplayer.util.Constants;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.List;

import timber.log.Timber;

public class RadioPlayerFragment extends BaseFragment implements
        View.OnClickListener, ServiceConnection{

    private static final String BUNDLE_STATE = "state";
    //private static final String BUNDLE_STATION_NAME = "name";
    //public static final String BUNDLE_QUEUE_POSITION = "queue_position";

    private TextView mStationTitle;
    private ImageButton mPlayStopBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private ProgressBar mProgressBar;
    private MediaControllerCompat mMediaController;
    //private boolean mFirstTimeIn = true;
    private List<Station> mQueue;
    private int mQueuePosition;
    private int mState;
    private View mView;
    private Application mAppContext;
    private String mName;

    public RadioPlayerFragment() {}

    public static RadioPlayerFragment newInstance(int queuePosition) {
        RadioPlayerFragment fragment = new RadioPlayerFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_QUEUE_POSITION, queuePosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // retrieve the queue & queue position
        mQueuePosition = getArguments().getInt(Constants.KEY_QUEUE_POSITION);
        mQueue = StationDataCache.getStationDataCache().getStationList();

        mAppContext = (Application) getActivity().getApplicationContext();

        // create the intent used to both start & bind to the service
        Intent intent = new Intent(mAppContext, PlaybackService.class);
        mAppContext.bindService(intent, this, 0);
        Timber.i("Binding Playback Service");
        mAppContext.startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppContext.unbindService(this);
        Timber.i("Unbinding Playback Service");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.content_player, container, false);

        mStationTitle = (TextView) mView.findViewById(R.id.station_title);
        setStationTitle();
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        // setup player controls elements
        mPlayStopBtn = (ImageButton) mView.findViewById(R.id.play_stop_button);
        mPlayStopBtn.setOnClickListener(this);
        mPrevBtn = (ImageButton) mView.findViewById(R.id.previous_button);
        mPrevBtn.setOnClickListener(this);
        mNextBtn = (ImageButton) mView.findViewById(R.id.next_button);
        mNextBtn.setOnClickListener(this);

        // hide prev/next btns to prevent use if starting from the first or last station
        if(mQueuePosition == 0) {
            mPrevBtn.setVisibility(View.GONE);
        }
        if(mQueuePosition == mQueue.size() - 1) {
            mNextBtn.setVisibility(View.GONE);
        }

        // ensure that the correct play/stop btn state is shown on rotation
        if(savedInstanceState != null) {
            mState = savedInstanceState.getInt(BUNDLE_STATE);
            if(mState == PlaybackStateCompat.STATE_BUFFERING) {
                mPlayStopBtn.setImageResource(R.drawable.action_stop);
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if(mState == PlaybackStateCompat.STATE_PLAYING)
                mPlayStopBtn.setImageResource(R.drawable.action_stop);
        }

        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mState = mMediaController.getPlaybackState().getState();
        outState.putInt(BUNDLE_STATE, mState);
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
                    // stop playback
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
                mMediaController = new MediaControllerCompat(mAppContext,
                        ((PlaybackService.ServiceBinder) service).getService().getMediaSessionToken());

                mMediaController.registerCallback(mMediaControllerCallback);
                mState = mMediaController.getPlaybackState().getState();
                Timber.i("Launching PlayerActivity, connecting to PlaybackService, current mState: %d", mState);

                // start playback as soon as player activity launches
                Timber.i("First time in");
                if(mState == PlaybackStateCompat.STATE_NONE || mState == PlaybackStateCompat.STATE_STOPPED) {
                    playFromStationUri();
                } else if(mState == PlaybackStateCompat.STATE_BUFFERING || mState == PlaybackStateCompat.STATE_PLAYING){
                    // if already playing, stop and start the new selected stn
                    mMediaController.getTransportControls().stop();
                    playFromStationUri();
                }

            } catch (RemoteException e) {
                Timber.e("Error instantiating the media controller: %s", e.getMessage());
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Timber.i("Service unexpectedly disconnected");
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

    // HELPER METHODS ///////////////////////////////////////////////////////

    @Subscribe
    public void getMessageEvent(PlaybackServiceEvent event) {
        String message = event.getMessage();
        switch (message) {
            case PlaybackServiceEvent.ON_PLAYBACK_ERROR:
            case PlaybackServiceEvent.ON_PLAYBACK_COMPLETION:
            case PlaybackServiceEvent.ON_AUDIO_FOCUS_LOSS:
            case PlaybackServiceEvent.ON_BECOMING_NOISY:
            case PlaybackServiceEvent.ON_NO_STREAM_FOUND:
                mPlayStopBtn.setImageResource(R.drawable.action_play);
            case PlaybackServiceEvent.ON_BUFFERING_COMPLETE:
                mProgressBar.setVisibility(View.GONE);
                displayMessage(message);
                break;
        }
    }

    @Subscribe
    public void getQueuePositionEvent(QueuePositionEvent event) {
        // update queue position and station title
        mQueuePosition = event.getQueuePosition();
        setStationTitle();
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

    private void setStationTitle() {
        mName = mQueue.get(mQueuePosition).getName();
        if(mName != null)
            mStationTitle.setText(mName);
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

    private void displayMessage(String message) {
        Utils.showSnackbar(mView, message);
    }


}
