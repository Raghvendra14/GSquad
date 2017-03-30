package com.example.android.gsquad.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.android.gsquad.R;
import com.example.android.gsquad.activity.MainActivity;
import com.example.android.gsquad.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.ExecutionException;

/**
 * Created by Raghvendra on 29-03-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            String senderUid = remoteMessage.getData().get("uid");
            String username = remoteMessage.getData().get("username");
            String profilePicUrl = remoteMessage.getData().get("profilePicUrl");

            if (!senderUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                sendNotification(username, profilePicUrl);
            }
        }
    }

    /*
    *   Generate a simple notification at receiver's side.
    */
    private void sendNotification(String username, String profilePicUrl) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.PARENT_IS_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap profilePic = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        try {
            profilePic = Glide.with(getApplicationContext())
                    .load(profilePicUrl)
                    .asBitmap()
                    .error(R.drawable.no_image)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(profilePic)
                .setContentText(username + " " + getResources().getString(R.string.request_sent))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
