package com.example.android.gsquad.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.gsquad.utils.Constants;
import com.example.android.gsquad.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

/**
 * Created by Raghvendra on 30-03-2017.
 */

public class DeleteFirebaseIDService extends IntentService {
    public static final String TAG = DeleteFirebaseIDService.class.getSimpleName();

    public DeleteFirebaseIDService() {
        super("DeleteFirebaseIDService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
            Utils.saveToken(getApplicationContext(), Constants.ARG_FIREBASE_TOKEN, "");
            Log.d(TAG, "FirebaseInstanceId Deleted");
            String newId = FirebaseInstanceId.getInstance().getToken();
            Utils.saveToken(getApplicationContext(), Constants.ARG_FIREBASE_TOKEN, newId);
            Log.d(TAG, "New Id: " + newId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
