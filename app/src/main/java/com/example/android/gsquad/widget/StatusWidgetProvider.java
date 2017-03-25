package com.example.android.gsquad.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.gsquad.R;
import com.example.android.gsquad.activity.MainActivity;
import com.example.android.gsquad.utils.Constants;

/**
 * Created by Raghvendra on 24-03-2017.
 */

public class StatusWidgetProvider extends AppWidgetProvider {
    public static String ONLINE_DATA_PUSH = "Online";
    public static String AWAY_DATA_PUSH = "Away";
    public static String OFFLINE_DATA_PUSH = "Offline";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, StatusService.class));
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_status);

        Intent launchIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

        Intent dataStorageIntent = new Intent(context, StatusWidgetProvider.class);
        dataStorageIntent.setAction(ONLINE_DATA_PUSH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, dataStorageIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.online_image_view, pendingIntent);

        dataStorageIntent = new Intent(context, StatusWidgetProvider.class);
        dataStorageIntent.setAction(ONLINE_DATA_PUSH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, dataStorageIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.online_status_text_view, pendingIntent);

        dataStorageIntent = new Intent(context, StatusWidgetProvider.class);
        dataStorageIntent.setAction(AWAY_DATA_PUSH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, dataStorageIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.away_image_view, pendingIntent);

        dataStorageIntent = new Intent(context, StatusWidgetProvider.class);
        dataStorageIntent.setAction(AWAY_DATA_PUSH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, dataStorageIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.away_status_text_view, pendingIntent);

        dataStorageIntent = new Intent(context, StatusWidgetProvider.class);
        dataStorageIntent.setAction(OFFLINE_DATA_PUSH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, dataStorageIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.offline_image_view, pendingIntent);

        dataStorageIntent = new Intent(context, StatusWidgetProvider.class);
        dataStorageIntent.setAction(OFFLINE_DATA_PUSH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, dataStorageIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.offline_status_text_view, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Constants.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, StatusService.class));
        } else if (intent.getAction().equals(ONLINE_DATA_PUSH)) {
            Intent actionIntent = new Intent(context, DataService.class);
            actionIntent.putExtra(Constants.ONLINE_DATA_PUSH, ONLINE_DATA_PUSH);
            context.startService(actionIntent);
        } else if (intent.getAction().equals(AWAY_DATA_PUSH)) {
            Intent actionIntent = new Intent(context, DataService.class);
            actionIntent.putExtra(Constants.AWAY_DATA_PUSH, AWAY_DATA_PUSH);
            context.startService(actionIntent);
        } else if (intent.getAction().equals(OFFLINE_DATA_PUSH)) {
            Intent actionIntent = new Intent(context, DataService.class);
            actionIntent.putExtra(Constants.OFFLINE_DATA_PUSH, OFFLINE_DATA_PUSH);
            context.startService(actionIntent);
        }
    }
}
