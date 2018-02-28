package com.gemi.chat_me.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import com.gemi.chat_me.R;

public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.widgetName, getWidgetName(context));
        views.setTextViewText(R.id.widgetStatus, getWidgetStatus(context));
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        if (!getWidgetImage(context).equals("")) {
            Picasso.with(context).load(getWidgetImage(context)).into(views, R.id.widgetImage, ids);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static String getWidgetName(Context x) {
        return PreferenceManager.getDefaultSharedPreferences(x).getString("name", "user name");
    }

    static String getWidgetStatus(Context x) {
        return PreferenceManager.getDefaultSharedPreferences(x).getString("status", "status");
    }

    static String getWidgetImage(Context x) {
        return PreferenceManager.getDefaultSharedPreferences(x).getString("image", "image");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}