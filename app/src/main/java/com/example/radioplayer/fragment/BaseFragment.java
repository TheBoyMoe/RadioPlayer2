package com.example.radioplayer.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.BaseEvent;
import com.squareup.otto.Bus;

public class BaseFragment extends Fragment {

    public BaseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getAppBus().unregister(this);
    }

    protected void postToBus(BaseEvent event) {
        RadioPlayerApplication.postToBus(event);
    }

    protected Bus getAppBus() {
        return RadioPlayerApplication.getInstance().getBus();
    }

}
