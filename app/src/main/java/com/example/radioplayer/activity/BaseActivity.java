package com.example.radioplayer.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.BaseEvent;
import com.squareup.otto.Bus;

public class BaseActivity extends AppCompatActivity{

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // setup the toolbar
    protected void setToolbar(int toolbarId) {
        mToolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(mToolbar);
    }

    // add the toolbar and set the overflow icon for 'older' apis
    protected void setToolbarOnActivity(int toolbarId) {
        setToolbar(toolbarId);
        if(getSupportActionBar() != null) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.action_overflow));
            }
        }
    }

    // add the toolbar, back 'arrow' and overflow icon to be consistent across all apis
    protected void setToolbarOnChildActivity(int toolbar) {
        setToolbar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.action_overflow));
                mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.action_back));
            }
        }
    }

    // setup the event bus
    @Override
    protected void onResume() {
        super.onResume();
        getAppBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getAppBus().unregister(this);
    }

    protected Bus getAppBus() {
        return RadioPlayerApplication.getInstance().getBus();
    }

    protected void postToBus(BaseEvent event) {
        RadioPlayerApplication.postToBus(event);
    }

}
