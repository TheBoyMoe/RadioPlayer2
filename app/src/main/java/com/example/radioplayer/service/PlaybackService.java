package com.example.radioplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.PlaybackServiceEvent;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.util.Utils;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class PlaybackService extends Service implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener{

    private static final String LOG_TAG = "PlaybackService";
    //public static final String EXTRA_STATION = "station";
    public static final String EXTRA_STATION_URI = "station_uri";
    public static final String EXTRA_STATION_NAME = "station_name";
    public static final String EXTRA_STATION_SLUG = "station_slug";
    public static final String EXTRA_STATION_COUNTRY = "station_country";
    public static final String EXTRA_STATION_IMAGE_URL = "station_image_url";
    public static final String EXTRA_STATION_THUMB_URL = "station_thumb_url";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_STOP = "updateSession";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PREV = "prev";

    private WifiManager.WifiLock mWifiLock;
    private AudioManager mAudioManager;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat mPlaybackState;
    private MediaControllerCompat mMediaController;
    private MediaPlayer mMediaPlayer;
    private Binder mBinder = new ServiceBinder();
    //private int mCurrentQueueIndex = 0;
    //private List<Station> mPlayingQueue;

    private final IntentFilter mNoisyIntentFilter =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private final BroadcastReceiver mNoisyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                // stop playback
                mMediaController.getTransportControls().stop();
                Timber.i("Headphones removed");
                RadioPlayerApplication.postToBus(new PlaybackServiceEvent(PlaybackServiceEvent.ON_BECOMING_NOISY));
            }
        }
    };


    public PlaybackService() {}

    // on binding to the service, return a reference to the service via the binder obj
    public class ServiceBinder extends Binder {

        public ServiceBinder() {}

        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public MediaSessionCompat.Token getMediaSessionToken() {
        return mMediaSession.getSessionToken();
    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if(startIntent != null && startIntent.getAction() != null) {

            Timber.i("Starting PlaybackService");
            switch (startIntent.getAction()) {
                case ACTION_PLAY:
                    Timber.i("Calling play");
                    mMediaController.getTransportControls().play();
                    break;
                case ACTION_STOP:
                    Timber.i("Calling updateSession");
                    mMediaController.getTransportControls().stop();
                    break;
                case ACTION_NEXT:
                    Timber.i("Calling next");
                    mMediaController.getTransportControls().skipToNext();
                    break;
                case ACTION_PREV:
                    Timber.i("Calling prev");
                    mMediaController.getTransportControls().skipToPrevious();
                    break;
            }
        }

        // receives and handles all media button key events and forwards
        // to the media session which deals with them in its callback methods
        MediaButtonReceiver.handleIntent(mMediaSession, startIntent);

        return super.onStartCommand(startIntent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlaybackState = updatePlaybackState(PlaybackStateCompat.STATE_NONE);

        // instantiate the media session
        mMediaSession = new MediaSessionCompat(this, LOG_TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setCallback(new MediaSessionCallback());

        // set the initial playback state
        mMediaSession.setPlaybackState(mPlaybackState);

        // get and instance of the audio manager
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // instantiate the media player
        initMediaPlayer();

        // instantiate the media controller
        try {
            mMediaController = new MediaControllerCompat(this, mMediaSession.getSessionToken());
        } catch (RemoteException e) {
            Timber.e("Error instantiating Media Controller: %s", e.getMessage());
        }

        // create the wifi lock
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, LOG_TAG);

        // register the event bus to enable event posting
        RadioPlayerApplication.getInstance().getBus().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RadioPlayerApplication.getInstance().getBus().unregister(this);

        Timber.i("Releasing resources");
        releaseResources();
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        // if we've lost focus, updateSession playback
        if(focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

            Timber.i("Focus lost, stopping playback");
            mMediaPlayer.stop();
            updateSession(PlaybackStateCompat.STATE_STOPPED, PlaybackServiceEvent.ON_AUDIO_FOCUS_LOSS);
            updateNotification(); // FIXME ??
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Timber.i("Buffering complete");
        // request audio focus
        int audioFocus = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        // register noisy broadcast receiver
        registerReceiver(mNoisyBroadcastReceiver, mNoisyIntentFilter);

        // if we've gained focus, start playback
        if(audioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Timber.i("Gained audio focus, starting playback");
            mMediaPlayer.start();

            // set media session obj as the target for media buttons
            mMediaSession.setActive(true);

            // update playback state
            mPlaybackState = updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
            mMediaSession.setPlaybackState(mPlaybackState);
            updateNotification();

            // post event, allowing the PlayerActivity to hide the progress bar
            RadioPlayerApplication.postToBus(new PlaybackServiceEvent(PlaybackServiceEvent.ON_BUFFERING_COMPLETE));
        } else {
            Timber.i("Failed to gain audio focus");
            RadioPlayerApplication.postToBus(new PlaybackServiceEvent(PlaybackServiceEvent.ON_AUDIO_FOCUS_LOSS));
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Timber.i("Playback has come to an end");
        mMediaPlayer.reset();
        updateSession(PlaybackStateCompat.STATE_NONE, PlaybackServiceEvent.ON_PLAYBACK_COMPLETION);
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Timber.e("Media Player has encountered an error, code: %d", what);
        mMediaPlayer.reset();
        updateSession(PlaybackStateCompat.STATE_NONE, PlaybackServiceEvent.ON_PLAYBACK_ERROR);
        return true; // error handled
    }


    private final class MediaSessionCallback extends MediaSessionCompat.Callback {

        // impl media player methods you want your player to handle
        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            Uri uri = extras.getParcelable(EXTRA_STATION_URI);
            onPlayFromUri(uri, extras);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {

            try {
                int state = mPlaybackState.getState();
                if(state == PlaybackStateCompat.STATE_NONE || state == PlaybackStateCompat.STATE_STOPPED) {

                    Timber.i("MediaPlayer: %s", mMediaPlayer);
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(PlaybackService.this, uri);
                    mMediaPlayer.prepareAsync(); // calls onPrepared() when complete
                    Timber.i("Buffering audio stream");
                    mPlaybackState = updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING);
                    mMediaSession.setPlaybackState(mPlaybackState);
                    // set the station metadata
                    mMediaSession.setMetadata(new MediaMetadataCompat.Builder()
                                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, extras.getString(EXTRA_STATION_NAME))
                                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, extras.getString(EXTRA_STATION_SLUG))
                                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, extras.getString(EXTRA_STATION_COUNTRY))
                                    .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, extras.getString(EXTRA_STATION_IMAGE_URL))
                                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, extras.getString(EXTRA_STATION_THUMB_URL))
                                    .build());
                    updateNotification();
                    // acquire wifi lock to prevent wifi going to sleep while playing
                    mWifiLock.acquire();

                }

            } catch (IOException e) {
                Timber.e("Error buffering audio stream");
            }

        }


        @Override
        public void onStop() {
            int state = mPlaybackState.getState();
            if(state == PlaybackStateCompat.STATE_PLAYING ||
                    state == PlaybackStateCompat.STATE_BUFFERING) {

                Timber.i("Stopping audio playback");
                mMediaPlayer.stop();
                updateSession(PlaybackStateCompat.STATE_STOPPED, PlaybackServiceEvent.ON_STOP);
                updateNotification();

            }
            // if we're buffering post an event so the progress bar can be hidden
            if(state == mPlaybackState.STATE_BUFFERING) {
                RadioPlayerApplication.postToBus(new PlaybackServiceEvent(PlaybackServiceEvent.ON_BUFFERING_COMPLETE));
            }
        }


        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            // TODO
        }


        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            // TODO
        }

    }


    ///// HELPER METHODS /////////////////////////////////////////////////////////////////////////

    private void initMediaPlayer() {
        Timber.i("Initializing media player");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        // tell the system it needs to stay on
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }


    // abandon focus, set media btn target to false, unregister noisy receiver and update playback state
    private void updateSession(int playbackState, String event) {
        mAudioManager.abandonAudioFocus(this);
        mMediaSession.setActive(false);
        unregisterReceiver(mNoisyBroadcastReceiver);

        mPlaybackState = updatePlaybackState(playbackState);
        mMediaSession.setPlaybackState(mPlaybackState);
        RadioPlayerApplication.postToBus(new PlaybackServiceEvent(event));
    }

    private void releaseMediaPlayer() {
        if(mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void releaseResources() {

        releaseMediaPlayer();

        if(mWifiLock.isHeld()) {
            mWifiLock.release();
            mWifiLock = null;
        }

        if (mMediaSession != null) {
            mMediaSession.release();
            mMediaSession = null;
        }
    }

    private PlaybackStateCompat updatePlaybackState(int playbackState) {
        return new PlaybackStateCompat.Builder()
                .setState(playbackState, 0, 1.0f)
                .build();
    }


    // TODO Notification & Notification.Builder
    private void updateNotification() {
        Timber.i("Update notification, if one existed");
    }



}
