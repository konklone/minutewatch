package com.extendsandroid.minutewatch.publish.receivers;

import java.util.Calendar;

import com.extendsandroid.minutewatch.publish.CallConfirm;
import com.extendsandroid.minutewatch.publish.R;
import com.extendsandroid.minutewatch.publish.Settings;
import com.extendsandroid.minutewatch.publish.services.MonitorCalls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CallReceiver extends BroadcastReceiver {
	private Context mContext;
	private String[] emergencyNumbers;
	
	private int nightTimeMinutesBegins;
	private int catchHoursBegins;
	private int startCatching;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		this.emergencyNumbers = mContext.getResources().getStringArray((R.array.emergency_numbers));
		
		if(Settings.getOverride(mContext))
			confirmCall(intent);
	}
	
	public void confirmCall(Intent intent) {
		String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		
		Intent i = new Intent();
		i.setClassName("com.extendsandroid.minutewatch.publish", "com.extendsandroid.minutewatch.publish.CallConfirm");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Bundle extras = new Bundle();
		extras.putString(CallConfirm.PHONE_KEY, number); 
		i.putExtras(extras);
		
		// cut the phone call and show our screen instead
		if (startCallConfirm(number)) {
			abortBroadcast();
			mContext.startActivity(i);
		} else {
			MonitorCalls.setLastOutgoingNumber(number);
			Settings.saveTransmitCall(mContext, false);
		}
	}
	
	public boolean startCallConfirm(String aNumber) {
		nightTimeMinutesBegins = Settings.getNightTimeBeginsValue(mContext);
		catchHoursBegins = Settings.getCatchBetween(mContext);
		startCatching = nightTimeMinutesBegins - catchHoursBegins;
		
		if (shouldTransmitCall())
			return false;
		if (!(Settings.getOverride(mContext)))
			return false;
		if (isEmergencyNumber(aNumber))
			return false;
		if (isWhitelistedNumber(aNumber))
			return false;
		else
			return catchableTime();
	}
	
	public boolean catchableTime() {
		if (Settings.getHasFreeNightTimeMinutes(mContext)) {
			if (Settings.getHasFreeWeekendMinutes(mContext))
				return catchableHours() && isNotTheWeekend();
			else
				return catchableHours();
		}
		else return false;
	}
	
	public boolean catchableHours() {
		return theHour() >= startCatching  && theHour() < nightTimeMinutesBegins ? true : false;
		
	}
	public boolean isEmergencyNumber(String aNumber) {
		return testForNumber(emergencyNumbers, aNumber) ?  true : false;
	}
	
	public boolean isWhitelistedNumber(String aNumber) {
		return testForNumber(MonitorCalls.whitelistedNumbers, aNumber) ?  true : false;
	}
	
	public static int theHour() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public boolean shouldTransmitCall() {
		return Settings.getTransmitCall(mContext) ? true  : false;
	}
	
	public boolean testForNumber(String[] array, String number) {
		int i;
		for (i=0; i<array.length; i++) // for each element,
			if(array[i].equals(number)) // if it finds the item?
				break;
	    if (i == array.length) // It has not found the number because the loop ran to completion
	    	return false; 
	    else
	    	return true;
    }
	
    public boolean isNotTheWeekend() {
    	Calendar now = Calendar.getInstance();
    	switch(now.get(Calendar.DAY_OF_WEEK)) {
    	case Calendar.SUNDAY:
    		return false;
    	case Calendar.SATURDAY:
    		return false;
    	default: return true ;
    	}
    }
    
}