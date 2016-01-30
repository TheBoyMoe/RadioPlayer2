package com.example.radioplayer;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.radioplayer.event.BaseEvent;
import com.squareup.otto.Bus;

import timber.log.Timber;

public class RadioPlayerApplication extends Application{

    private static RadioPlayerApplication sInstance;
    private static Bus sBus;

    public static RadioPlayerApplication getInstance() {
        if(sInstance == null) {
            sInstance = new RadioPlayerApplication();
            sBus = new ApplicationBus();
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // enable Timber debugging
        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }

    public Bus getBus() {
        return sBus;
    }

    // post any type of event from anywhere in the app
    public static void postToBus(BaseEvent event) {
        getInstance().getBus().post(event);
    }


    // enable posting of events from either the main or background threads
    public static class ApplicationBus extends Bus {
        private final Handler mainThread = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        post(event);
                    }
                });
            }
        }
    }


}
