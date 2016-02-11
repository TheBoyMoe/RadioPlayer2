package com.example.radioplayer.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import timber.log.Timber;

public class Utils {

    private Utils() {
        throw new AssertionError();
    }

    // hide the keyboard on executing search
    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    // Check that a connection is available
    public  static boolean isClientConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return  activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // MediaPlayer state codes - for debugging
    public static void getMediaPlayerStateCodes() {
        Timber.i("Playback state none, code %d", PlaybackStateCompat.STATE_NONE);
        Timber.i("Playback state stopped, code %d", PlaybackStateCompat.STATE_STOPPED);
        Timber.i("Playback state paused, code %d", PlaybackStateCompat.STATE_PAUSED);
        Timber.i("Playback state playing, code %d", PlaybackStateCompat.STATE_PLAYING);
        Timber.i("Playback state fast forwarding, code %d", PlaybackStateCompat.STATE_FAST_FORWARDING);
        Timber.i("Playback state rewinding, code %d", PlaybackStateCompat.STATE_REWINDING);
        Timber.i("Playback state buffering, code %d", PlaybackStateCompat.STATE_BUFFERING);
        Timber.i("Playback state error, code %d", PlaybackStateCompat.STATE_ERROR);
        Timber.i("Playback state connecting, code %d", PlaybackStateCompat.STATE_CONNECTING);
        Timber.i("Playback state skip to previous, code %d", PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS);
        Timber.i("Playback state skip to next, code %d", PlaybackStateCompat.STATE_SKIPPING_TO_NEXT);
    }


}
