package com.extendsandroid.minutewatch.publish.receivers;

import com.extendsandroid.minutewatch.publish.BillCycle;
import com.extendsandroid.minutewatch.publish.Settings;
import com.extendsandroid.minutewatch.publish.services.MonitorCalls;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BillCycleReceiver extends BroadcastReceiver {
	Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {	
		this.mContext = context;
		BillCycle.updateBillCycleEndDate(context);
		resetBillCycleAlarm();
	}
	
	private void resetBillCycleAlarm() {
		Context context = mContext;
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
		PendingIntent notifierIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(MonitorCalls.ACTION_CREATE_ROLLOVER_DATE), 0);
        alarmManager.set(AlarmManager.RTC, Settings.getRollOverDateInMillis(context), notifierIntent);
	}
}