package com.example.android.gsquad.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.android.gsquad.R;
import com.example.android.gsquad.utils.Constants;

/**
 * Created by Raghvendra on 26-02-2017.
 */

public class SplashActivity extends AppCompatActivity {

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
        }, Constants.SPLASH_DISPLAY_LENGTH);

    }
}
