package com.extendsandroid.minutewatch.publish.preferences;
 
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.extendsandroid.minutewatch.publish.Settings;
import com.extendsandroid.minutewatch.publish.services.MonitorCalls;


public class NightTimeBeginsPreference extends ListPreference {
	private AlarmManager alarmManager;
	String oldValue;
    Context mContext;
	
	public NightTimeBeginsPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.oldValue =  "" + Settings.getNightTimeBeginsValue(context);
	}

	public  NightTimeBeginsPreference(Context context) {
		super(context);
	}
  
	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    super.onDialogClosed(positiveResult);
	    Context context = mContext;
	    String newValue = getSharedPreferences().getString(getKey(), "1");
	    if (!(oldValue.equals(newValue))) {
	    	resetDelayedCallsAlarm(context);
	    }
	}
	
	public void resetDelayedCallsAlarm(Context context) {
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent notifierIntent = PendingIntent.getBroadcast(context, 0, new Intent(MonitorCalls.ACTION_NOTIFY_DELAYED_CALLS), 0);
	    alarmManager.cancel(notifierIntent);
	    alarmManager.set(AlarmManager.RTC, Settings.getNextNightTimeInMillis(context), notifierIntent);
	}
   
}