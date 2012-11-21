package com.android.alarmclock;

import com.android.deskclock.AlarmClock;
import com.android.deskclock.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class Shendu_Digital_Clock_Widget extends AppWidgetProvider{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shendu_digital_appwidget_linear);
		views.setOnClickPendingIntent(R.id.shendu_widget_digital,
                  PendingIntent.getActivity(context, 0,
                      new Intent(context, AlarmClock.class), 0));
       appWidgetManager.updateAppWidget(appWidgetIds, views);
	}

}
