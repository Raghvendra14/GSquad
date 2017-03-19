package com.example.android.gsquad;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class GSquad extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
