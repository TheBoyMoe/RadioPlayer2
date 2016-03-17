package com.example.radioplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.radioplayer.R;

public class SplashScreenActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // create a new thread that will launch the app after 2 secs
        Thread appLauncher = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
            }
        };
        appLauncher.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // kill the activity
        finish();
    }


}
