package com.example.android.gsquad.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.android.gsquad.R;
import com.example.android.gsquad.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

/**
 * Created by Raghvendra on 24-03-2017.
 */

public class StatusService extends IntentService {
    private String mFirebaseId = "";
    private Bitmap mImageBitmap = null;
    private static final String TAG = StatusService.class.getSimpleName();
    private String mUserName = "Anonymous";
    private RemoteViews mRemoteViews;

    public StatusService() {
        super("StatusService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.widget_status);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StatusWidgetProvider.class));
        String imageUrl = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mFirebaseId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            imageUrl = Utils.getProfilePicUrl(FirebaseAuth.getInstance().getCurrentUser(), this);
            try {
                mImageBitmap = Glide.with(this)
                        .load(imageUrl)
                        .asBitmap()
                        .error(R.drawable.no_image)
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Error retrieving user pic from " + imageUrl, e);
            }
            DatabaseReference mUserDataReference = FirebaseDatabase.getInstance().getReference()
                    .child("status");
            mUserDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(mFirebaseId)) {
                            String status = (String) snapshot.getValue();
                            for (int appWidgetId : appWidgetIds) {
                                setIconActivated(status, appWidgetManager, appWidgetId);
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mRemoteViews.setImageViewResource(R.id.widget_user_pic, R.drawable.no_image);
            mRemoteViews.setTextViewText(R.id.widget_username, getResources().getString(R.string.anonymous_user));
            mRemoteViews.setTextColor(R.id.online_status_text_view, Color.BLACK);
            mRemoteViews.setTextColor(R.id.away_status_text_view, Color.BLACK);
            mRemoteViews.setTextColor(R.id.offline_status_text_view, Color.BLACK);
            mRemoteViews.setImageViewResource(R.id.online_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
            mRemoteViews.setImageViewResource(R.id.away_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
            mRemoteViews.setImageViewResource(R.id.offline_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
            appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
        }
    }

    private void setIconActivated(String string, AppWidgetManager appWidgetManager, int appWidgetId) {

        if (mImageBitmap != null) {
            mRemoteViews.setImageViewBitmap(R.id.widget_user_pic, mImageBitmap);
        } else {
            mRemoteViews.setImageViewResource(R.id.widget_user_pic, R.drawable.no_image);
        }

        mRemoteViews.setTextViewText(R.id.widget_username, mUserName);

        switch (string) {
            case "Online": {
                mRemoteViews.setTextColor(R.id.online_status_text_view, getResources().getColor(R.color.green));
                mRemoteViews.setTextColor(R.id.away_status_text_view, Color.BLACK);
                mRemoteViews.setTextColor(R.id.offline_status_text_view, Color.BLACK);
                mRemoteViews.setImageViewResource(R.id.online_image_view, R.drawable.ic_radio_button_checked_green_24dp);
                mRemoteViews.setImageViewResource(R.id.away_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                mRemoteViews.setImageViewResource(R.id.offline_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                break;
            }
            case "Away": {
                mRemoteViews.setTextColor(R.id.away_status_text_view, getResources().getColor(R.color.orange));
                mRemoteViews.setTextColor(R.id.online_status_text_view, Color.BLACK);
                mRemoteViews.setTextColor(R.id.offline_status_text_view, Color.BLACK);
                mRemoteViews.setImageViewResource(R.id.away_image_view, R.drawable.ic_radio_button_checked_orange_24dp);
                mRemoteViews.setImageViewResource(R.id.online_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                mRemoteViews.setImageViewResource(R.id.offline_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                break;
            }
            case "Offline": {
                mRemoteViews.setTextColor(R.id.offline_status_text_view, getResources().getColor(R.color.red));
                mRemoteViews.setTextColor(R.id.away_status_text_view, Color.BLACK);
                mRemoteViews.setTextColor(R.id.online_status_text_view, Color.BLACK);
                mRemoteViews.setImageViewResource(R.id.offline_image_view, R.drawable.ic_radio_button_checked_red_24dp);
                mRemoteViews.setImageViewResource(R.id.away_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                mRemoteViews.setImageViewResource(R.id.online_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                break;
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }
}
