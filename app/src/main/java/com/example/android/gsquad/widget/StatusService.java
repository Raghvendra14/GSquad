package com.example.android.gsquad.widget;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.gsquad.model.UserBasicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Raghvendra on 24-03-2017.
 */

public class StatusService extends IntentService {

    public StatusService() {
        super("StatusService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String firebaseId = "Anonymous";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        DatabaseReference mUserDataReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseId);
        mUserDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserBasicInfo userBasicInfo = dataSnapshot.getValue(UserBasicInfo.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
