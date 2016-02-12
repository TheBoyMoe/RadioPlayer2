package com.example.radioplayer.event;

public class PlaybackServiceEvent extends MessageEvent{

    public static final String ON_BUFFERING_COMPLETE = "Buffering complete, playback starting";
    public static final String ON_PLAYBACK_COMPLETION = "Playback stopped, stream complete";
    public static final String ON_PLAYBACK_ERROR = "Media error encountered";
    public static final String ON_AUDIO_FOCUS_LOSS = "Playback stopped, focus has been lost";
    public static final String ON_STOP = "Playback stopped";
    public static final String ON_BECOMING_NOISY = "Playback stopped, headphones removed";
    public static final String ON_NO_STREAM_FOUND = "No stream found, try a different station";

    public PlaybackServiceEvent(String message) {
        super(message);
    }

}
