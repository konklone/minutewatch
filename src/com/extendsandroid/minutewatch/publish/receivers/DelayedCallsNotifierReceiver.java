package com.extendsandroid.minutewatch.publish.receivers;

import com.extendsandroid.minutewatch.publish.R;
import com.extendsandroid.minutewatch.publish.Settings;
import com.extendsandroid.minutewatch.publish.database.MinuteDatabaseAdapter;
import com.extendsandroid.minutewatch.publish.services.MonitorCalls;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class DelayedCallsNotifierReceiver extends BroadcastReceiver {
	
	Context mContext;
	private Cursor dbCursor;
	private MinuteDatabaseAdapter minuteDatabaseAdapter;
	private NotificationManager notificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		
		notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE); 
		minuteDatabaseAdapter = new MinuteDatabaseAdapter(mContext);
        
        notifyDelayedCalls();
        resetAlarm();
	}
	
	private void notifyDelayedCalls() {
		minuteDatabaseAdapter.open();
		
		dbCursor = minuteDatabaseAdapter.fetchAllDelayedCalls();
		dbCursor.requery();
		
		int i  = 1;
		if (dbCursor.moveToFirst()) {
			do {
		    	 String PhoneNumber = dbCursor.getString(MinuteDatabaseAdapter.PHONE_NUMBER_COLUMN);
		    	 notifyCall(PhoneNumber, i);
		    	 i = i + 1;
		    } while(dbCursor.moveToNext());
		}
		// Truncate Table
		minuteDatabaseAdapter.truncateDelayedCallsTable();
		minuteDatabaseAdapter.close();
	}
	  
	public void notifyCall(String aPhoneNumber, int i) {
		Context context = this.mContext;
		
		// Copy for the notification
		String tickerText = "Make Call: " + aPhoneNumber; 
		String expandedText = "Calls are now free"; 
		String expandedTitle = "Tap to call: " + aPhoneNumber; 
		
		// The notification, when clicked, should dial the person immediately 
		Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + aPhoneNumber)); 
		PendingIntent launchIntent = PendingIntent.getActivity(context, 0, intent, 0); 
		
		Notification notification = new Notification(R.drawable.icon, tickerText, System.currentTimeMillis());
		notification.setLatestEventInfo(context, expandedTitle, expandedText, launchIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL; 
		
		notificationManager.notify(i, notification); 
	}
	
	private void resetAlarm() {
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
        PendingIntent notifierIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(MonitorCalls.ACTION_NOTIFY_DELAYED_CALLS), 0);
        alarmManager.set(AlarmManager.RTC, Settings.getNextNightTimeInMillis(mContext), notifierIntent);
	}

}