package com.example.android.gsquad.fcm;

import android.util.Log;

import com.example.android.gsquad.utils.Constants;
import com.example.android.gsquad.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Raghvendra on 29-03-2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        // TODO : save it in shared preference if necessary
        Utils.saveToken(getApplicationContext(), Constants.ARG_FIREBASE_TOKEN, token);
    }
}
