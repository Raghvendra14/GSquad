package com.example.android.gsquad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Raghvendra on 26-02-2017.
 */

public class SplashActivity extends AppCompatActivity {

    /* Duration of displaying Splash screen */
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /* New Handler to start the main activity */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create the intent that will start the MainActivity after 1 seconds*/
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
