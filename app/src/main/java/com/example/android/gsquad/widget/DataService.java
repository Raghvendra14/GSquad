package com.example.android.gsquad.widget;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.gsquad.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Raghvendra on 25-03-2017.
 */

public class DataService extends IntentService {
    private String mFirebaseUserId = "";

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String newStatus = "";
        if (intent.hasExtra(Constants.ONLINE_DATA_PUSH)) {
            newStatus = intent.getStringExtra(Constants.ONLINE_DATA_PUSH);
        } else if (intent.hasExtra(Constants.AWAY_DATA_PUSH)) {
            newStatus = intent.getStringExtra(Constants.AWAY_DATA_PUSH);
        } else if (intent.hasExtra(Constants.OFFLINE_DATA_PUSH)) {
            newStatus = intent.getStringExtra(Constants.OFFLINE_DATA_PUSH);
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mFirebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mUserStatusDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("status").child(mFirebaseUserId);
            mUserStatusDatabase.setValue(newStatus);
            Intent dataUpdatedIntent = new Intent(Constants.ACTION_DATA_UPDATED)
                    .setPackage(getPackageName());
            sendBroadcast(dataUpdatedIntent);
        }
    }
}
