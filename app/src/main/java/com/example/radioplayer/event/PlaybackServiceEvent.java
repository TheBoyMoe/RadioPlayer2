package com.example.radioplayer.event;

public class PlaybackServiceEvent extends MessageEvent{

    public static final String ON_BUFFERING_COMPLETE = "on_loading_complete";
    public static final String ON_PLAYBACK_COMPLETION = "on_stream_completion";
    public static final String ON_PLAYBACK_ERROR = "on_media_error";
    public static final String ON_AUDIO_FOCUS_LOSS = "on_audio_focus_loss";
    public static final String ON_STOP = "on_stop";
    public static final String ON_BECOMING_NOISY = "on_becoming_noisy";

    public PlaybackServiceEvent(String message) {
        super(message);
    }

}
