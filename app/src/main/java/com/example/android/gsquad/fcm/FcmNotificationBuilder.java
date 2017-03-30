package com.example.android.gsquad.fcm;

import android.util.Log;

import com.example.android.gsquad.model.Data;
import com.example.android.gsquad.model.Notification;
import com.example.android.gsquad.rest.ApiClient;
import com.example.android.gsquad.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Raghvendra on 29-03-2017.
 */

public class FcmNotificationBuilder {
    private static final String TAG = FcmNotificationBuilder.class.getSimpleName();
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";

    private String mSenderUid;
    private String mUsername;
    private String mProfilePicUrl;
    private String mReceiverFirebaseToken;

    private FcmNotificationBuilder() {
    }

    public static FcmNotificationBuilder initialize() { return new FcmNotificationBuilder(); }

    public FcmNotificationBuilder senderUid(String uid) {
        mSenderUid = uid;
        return this;
    }

    public FcmNotificationBuilder username(String username) {
        mUsername = username;
        return this;
    }

    public FcmNotificationBuilder profilePicUrl(String profilePicUrl) {
        mProfilePicUrl = profilePicUrl;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public void send() {
        Notification notification = new Notification();
        Data data = new Data();
        data.setUid(mSenderUid);
        data.setProfilePicUrl(mProfilePicUrl);
        data.setUsername(mUsername);
        notification.setData(data);
        notification.setTo(mReceiverFirebaseToken);
        ApiInterface apiInterface =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Notification> call = apiInterface.createNotification(BASE_URL, notification);
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
