package com.example.android.gsquad.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.gsquad.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class Utils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getProfilePicUrl(FirebaseUser user, Context context) {
        String photoUrl = "";
        for (UserInfo profile : user.getProviderData()) {
            if (profile.getProviderId().equals(context.getResources().getString(R.string.facebook_provider_id))) {
                String facebookUserId = profile.getUid();
                photoUrl = "https://graph.facebook.com/" + facebookUserId +
                        "/picture?height=500&width=500";
            } else if (profile.getProviderId().equals(context.getResources().getString(R.string.google_provider_id))) {
                photoUrl = profile.getPhotoUrl().toString();
            }
        }
        if (!photoUrl.isEmpty()) {
            return photoUrl;
        }
        return null;
    }
}
