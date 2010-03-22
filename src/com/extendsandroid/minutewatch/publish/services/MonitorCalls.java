package com.extendsandroid.minutewatch.publish.services;

import java.util.ArrayList;
import java.util.List;

import com.extendsandroid.minutewatch.publish.BillCycle;
import com.extendsandroid.minutewatch.publish.CallEndsListener;
import com.extendsandroid.minutewatch.publish.Settings;
import com.extendsandroid.minutewatch.publish.database.MinuteDatabaseAdapter;
import com.extendsandroid.minutewatch.publish.receivers.MinuteUsageNotifierReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MonitorCalls extends Service { 
	private static MinuteDatabaseAdapter minuteDatabaseAdapter;
	private static Cursor whitelistCursor;
	public static String[] whitelistedNumbers; 
	public static final String ACTION_NOTIFY_DELAYED_CALLS = "com.extendsandroid.minutewatch.publish.ACTION_NOTIFY_DELAYED_CALLS";
	public static final String ACTION_NOTIFY_MINUTE_USAGE = "com.extendsandroid.minutewatch.publish.ACTION_NOTIFY_MINUTE_USAGE";
	public static final String ACTION_CREATE_ROLLOVER_DATE = "com.extendsandroid.minutewatch.publish.ACTION_CREATE_ROLLOVER_DATE";

	
	private static String mLastOutgoingNumber;
	
	private Context mContext;
	private AlarmManager alarmManager;
  
	@Override 
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	private void setMinuteUsageAlarm() {
		Context context = mContext;
		PendingIntent usageIntent = PendingIntent.getBroadcast(this, 0, new Intent(MonitorCalls.ACTION_NOTIFY_MINUTE_USAGE), 0);
        long nextNoon = Settings.getNextSpecificHourInMillis(MinuteUsageNotifierReceiver.TIME_OF_DAY, context);
        alarmManager.set(AlarmManager.RTC, nextNoon, usageIntent);
	}
	
	private void setDelayedCallsAlarm() {
		Context context = mContext;
		PendingIntent notifierIntent = PendingIntent.getBroadcast(this, 0, new Intent(MonitorCalls.ACTION_NOTIFY_DELAYED_CALLS), 0);
        alarmManager.set(AlarmManager.RTC, Settings.getNextNightTimeInMillis(context), notifierIntent);
	}
	
	private void setBillCycleAlarm() {
		Context context = mContext;
		PendingIntent notifierIntent = PendingIntent.getBroadcast(this, 0, new Intent(MonitorCalls.ACTION_CREATE_ROLLOVER_DATE), 0);
        alarmManager.set(AlarmManager.RTC, Settings.getRollOverDateInMillis(context), notifierIntent);
	}
	
	public static void buildWhitelistedNumberArray() {
		List<String> list = new ArrayList<String>();
		whitelistCursor = minuteDatabaseAdapter.fetchAllWhitelistedNumbers();
		if (whitelistCursor.moveToFirst()) 
			do  { 
				String number = 
					whitelistCursor.getString(MinuteDatabaseAdapter.WHITELISTED_NUMBER_COLUMN);
				number = number.replaceAll("\\W", "");
				list.add(number);
			} while(whitelistCursor.moveToNext());
		
		whitelistedNumbers = list.toArray(new String[list.size()]);
	}
	
	public static String getLastOutgoingNumber() {
		return mLastOutgoingNumber;
	}
	
	public static void setLastOutgoingNumber(String number) {
		mLastOutgoingNumber = number;
	}
	
	@Override 
	public void onDestroy() { 
		super.onDestroy(); 
		minuteDatabaseAdapter.close(); 
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override 
	public void onCreate() {
		super.onCreate();
		
		mContext = getApplicationContext();
		
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
        CallEndsListener phoneListener = new CallEndsListener(getBaseContext());
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        
        minuteDatabaseAdapter = new MinuteDatabaseAdapter(this);
        minuteDatabaseAdapter.open();
        
        buildWhitelistedNumberArray();
        
        setDelayedCallsAlarm();
        setMinuteUsageAlarm();
        
        BillCycle.updateBillCycleEndDate(mContext);
        setBillCycleAlarm();
	}
}