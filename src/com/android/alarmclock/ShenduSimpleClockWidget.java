package com.android.alarmclock;


import java.util.Calendar;
import java.util.TimeZone;
import com.android.deskclock.AlarmClock;
import com.android.deskclock.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.widget.RemoteViews;

/**
 * @date 2013-2-1
 * @author huhailong huhailong@shendu.com
 * @project CM10_DeskClock
 * TODO for DeskClock simple widget
 */
public class ShenduSimpleClockWidget extends AppWidgetProvider {

	public static String TAG = "ShenduSimpleClock";
	private Time mTime = new Time();;
	private Calendar mCalendar = Calendar.getInstance();;
	private String mStrAMPM = "";
	private String mStrTimeHour = "";
	private String mStrTimeMinute = "";
	private final static String M24 = "kk:mm";
	private final static String M12 = "h:mm";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		notidifyDataChange(context, appWidgetIds);
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(context, ShenduSimpleClockService.class);
		context.startService(serviceIntent);
	}
	
	/** Update time instances by receiver */
	public void onTimeChanged(Context context,Intent intent) {
		if(!hasInstances(context)){
			return;
		}
		if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
			String tz = intent.getStringExtra("time-zone");
			mTime = new Time(TimeZone.getTimeZone(tz).getID());
		}
		notidifyDataChange(context,null);
	}
	
	/** Update all active widget instances by pushing changes */
	private void notidifyDataChange(Context context,int[] appWidgetIds){
		mTime.setToNow();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		String newTime = DateFormat.format(get24HourMode(context),mCalendar).toString();
		if(!android.text.format.DateFormat.is24HourFormat(context)){
			mStrAMPM = mCalendar.get(Calendar.AM_PM)==0 ? "AM":"PM";
		}else{
			mStrAMPM = "";
		}
		if(newTime.indexOf(":") == 1){
			newTime = "0"+newTime;
		}
		String time[] = newTime.split(":");
		mStrTimeHour = time[0];
		mStrTimeMinute = time[1];
    	RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.shendu_simple_appwidget_linear);
        views.setTextViewText(R.id.shendu_widget_simple_hour_text_id, mStrTimeHour);
        views.setTextViewText(R.id.shendu_widget_simple_minute_text_id, mStrTimeMinute);
        views.setTextViewText(R.id.shendu_widget_simple_ampm_text_id, mStrAMPM);
        pushUpdate(context, appWidgetIds, views);
	}
	

	/**
	 * @return true if clock is set to 24-hour mode
	 */
	private String get24HourMode(Context context) {
		return android.text.format.DateFormat.is24HourFormat(context)?M24:M12;
	}
	
	/**
     * Check against {@link AppWidgetManager} if there are any instances of this widget.
     */
    private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        return (appWidgetIds.length > 0);
    }
	
	/**
	 * Update specific list of appWidgetIds if given, otherwise default to all
	 */
	private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		views.setOnClickPendingIntent(R.id.shendu_widget_simple_id,
				PendingIntent.getActivity(context, 0,new Intent(context, AlarmClock.class), 0));
        if (appWidgetIds != null) {
        	appWidgetManager.updateAppWidget(appWidgetIds, views);
        } else {
        	appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }
	
}
