package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

/**
 * Created by Brendan on 5/10/2016.
 */
public class StockWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_IDS_KEY ="mywidgetproviderwidgetids";
    public static final String WIDGET_DATA_KEY ="mywidgetproviderwidgetdata";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("on update", "update");
        // Cycles through array od IDs, one for each widget created by this provider
        for (int appWidgetId : appWidgetIds){
            Log.d("appwidgetid", String.valueOf(appWidgetId));

            // Create an intent to launch MyStocksActivity
            Intent intent = new Intent(context, StockWidgetService.class);
//            // Adding app widget Id to intent extras
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//
//            // Get the layout for the widget, attach Listener to widget
//            // Adapter connects to RemoteViewsService via intent to populate data
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setRemoteAdapter(R.id.widget_recycler_view, intent);

//            // EmptyView displayed if collection is empty
            views.setEmptyView(R.id.widget_recycler_view, R.id.widget_empty_view);

            updateWidget(context);
            // Perform update on current App Widget

            ComponentName component = new ComponentName(context, StockWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_recycler_view);
            appWidgetManager.updateAppWidget(component, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("on receive", "received");
        super.onReceive(context, intent);

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, StockIntentService.class));
    }

    public static void updateWidget(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StockWidgetProvider.class));
        Intent updateWidgetIntent = new Intent();
        updateWidgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateWidgetIntent.putExtra(WIDGET_IDS_KEY, appWidgetIds);
        context.sendBroadcast(updateWidgetIntent);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent intent = new Intent();
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(WIDGET_IDS_KEY, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_recycler_view, pendingIntent);
    }
}
