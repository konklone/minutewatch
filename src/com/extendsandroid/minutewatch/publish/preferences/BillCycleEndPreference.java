package com.extendsandroid.minutewatch.publish.preferences;
 
import com.extendsandroid.minutewatch.publish.BillCycle;
import com.extendsandroid.minutewatch.publish.Settings;
import com.extendsandroid.minutewatch.publish.services.MonitorCalls;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.ListPreference;
import android.util.AttributeSet;


public class BillCycleEndPreference extends ListPreference {
	private AlarmManager alarmManager;
    private Context mContext;
	
	public BillCycleEndPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public  BillCycleEndPreference(Context context) {
		super(context);
	}
  
	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    super.onDialogClosed(positiveResult);
	    Context context = mContext;
	    int billingDay = Integer.parseInt(getSharedPreferences().getString(getKey(), "1"));
	    long nextDate = BillCycle.nextBillingDate(context, billingDay, true);
	    Settings.setRollOverDatePreference(context, nextDate);
		Settings.setBillCycleEndsDate(context, nextDate);
		Settings.resetMinutesUsed(context);
		resetRolloverAlarm( context);
	}
	
	public void resetRolloverAlarm(Context context) {
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent notifierIntent = PendingIntent.getBroadcast(context, 0, new Intent(MonitorCalls.ACTION_CREATE_ROLLOVER_DATE), 0);
	    alarmManager.cancel(notifierIntent);
	    alarmManager.set(AlarmManager.RTC, Settings.getRollOverDateInMillis(context), notifierIntent);
	}
   
}