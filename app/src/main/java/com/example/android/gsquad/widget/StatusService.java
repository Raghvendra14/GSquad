package com.example.android.gsquad.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.example.android.gsquad.R;
import com.example.android.gsquad.activity.MainActivity;
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
    private String mFirebaseId = "";

    public StatusService() {
        super("StatusService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String userName = "Anonymous";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mFirebaseId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StatusWidgetProvider.class));
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
    }

    private void setIconActivated(String string, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_status);
        switch (string) {
            case "Online": {
                views.setTextColor(R.id.online_status_text_view, getResources().getColor(R.color.green));
                views.setTextColor(R.id.away_status_text_view, Color.BLACK);
                views.setTextColor(R.id.offline_status_text_view, Color.BLACK);
                views.setImageViewResource(R.id.away_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                break;
            }
            case "Away": {
                views.setTextColor(R.id.away_status_text_view, getResources().getColor(R.color.orange));
                views.setTextColor(R.id.online_status_text_view, Color.BLACK);
                views.setTextColor(R.id.offline_status_text_view, Color.BLACK);
                views.setImageViewResource(R.id.away_image_view, R.drawable.ic_radio_button_checked_orange_24dp);
                views.setImageViewResource(R.id.online_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                views.setImageViewResource(R.id.offline_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                break;
            }
            case "Offline": {
                views.setTextColor(R.id.offline_status_text_view, getResources().getColor(R.color.red));
                views.setTextColor(R.id.away_status_text_view, Color.BLACK);
                views.setTextColor(R.id.online_status_text_view, Color.BLACK);
                views.setImageViewResource(R.id.away_image_view, R.drawable.ic_radio_button_unchecked_black_24dp);
                break;
            }
        }
        Intent launchIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
