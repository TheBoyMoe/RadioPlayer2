package com.example.radioplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.radioplayer.model.Station;
import com.example.radioplayer.model.Stream;

import java.util.ArrayList;

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


    // retrieve the stream url from the station object with a status of 1 or greater
    public static String getStream(Station stn) {

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


    // launch the transition on devices with api 21+, ignored on older devices
    public static void launchActivity(Activity activity, Intent intent) {
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
    }


    // fade the supplied view  element - either in or out
    public static void fadeViewElement(final View view, final int visibility, int opacityStart, int opacityEnd) {

        Animation fadeOut = new AlphaAnimation(opacityStart, opacityEnd);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(300);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(visibility);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        view.startAnimation(fadeOut);
    }

}
