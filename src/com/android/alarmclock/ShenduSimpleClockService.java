package com.android.alarmclock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * @date 2013-3-14
 * @author huhailong huhailong@shendu.com
 * @project CM10_DeskClock
 * TODO: listener the time changed,time zone changed,then changed the deskclock widget display info
 */
public class ShenduSimpleClockService extends Service{

	public IBinder onBind(Intent intent) {
		return onBind(intent);
	}
	
	ShenduSimpleClockWidget mShenduSimpleClockWidget = new ShenduSimpleClockWidget();
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		registerReceiver(mIntentReceiver, intentFilter);
		return super.onStartCommand(intent, flags, startId);
	}
	
	public BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			mShenduSimpleClockWidget.onTimeChanged(context,intent);
		}
	};

}
