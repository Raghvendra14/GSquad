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
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_status);

            // Create an intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingResult = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingResult);

            context.startService(new Intent(context, StatusService.class));
////            setRemoteAdapter(context, views);
//
////            Intent clickIntentActivity = new Intent(context, MainActivity.class);
////            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
////                    .addNextIntentWithParentStack(clickIntentActivity)
////                    .getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
////            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
////            views.setEmptyView(R.id.widget_list, R.id.widget_empty);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    // TODO: We will put a hold on Broadcast Receiver for a while
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Constants.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, StatusService.class));
        }

    }

}
