package com.example.radioplayer;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.radioplayer.event.BaseEvent;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.timber.StethoTree;
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

        // initialize Stetho
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this)) // enable cli
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)) // enable chrome dev tools
                .build());

        // enable Timber debugging in debug build
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree(){
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    // adding the line number to the tag
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
            // show logs in the Chrome browser console log via Stetho (works with Timber 3.0.1)
            Timber.plant(new StethoTree());
        }

        // FIXME RadioPlayerActivity causes a memory leak
        // detect memory leaks
        // LeakCanary.install(this);
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
