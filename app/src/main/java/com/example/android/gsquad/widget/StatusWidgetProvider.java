package com.example.android.gsquad.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.android.gsquad.utils.Constants;

/**
 * Created by Raghvendra on 24-03-2017.
 */

public class StatusWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            context.startService(new Intent(context, StatusService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Constants.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, StatusService.class));
        }
    }
}
