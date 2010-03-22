package com.extendsandroid.minutewatch.publish.receivers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.extendsandroid.minutewatch.publish.MinuteWatch;
import com.extendsandroid.minutewatch.publish.R;
import com.extendsandroid.minutewatch.publish.Settings;
import com.extendsandroid.minutewatch.publish.services.MonitorCalls;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MinuteUsageNotifierReceiver extends BroadcastReceiver {

	public static final int TIME_OF_DAY = 12;
	public static final int EVERY_X_DAYS = 7;
	private static final int NOTIFICATION_ID = 1056;
	
	Context mContext;
	private NotificationManager notificationManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {		
		this.mContext = context;
		
		notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if (shouldNotify()) {
			notifyMinutes();
			storeNotified();
		}
		resetAlarm();
	}
	 
	private boolean shouldNotify() {
		long lastNotified = Settings.getLastMinutesUsedNotifierTime(mContext);
		long now = System.currentTimeMillis();
		return (now - lastNotified > (1000 * 60 * 60 * 24 * EVERY_X_DAYS));
	}
	
	private void storeNotified() {
		Settings.setLastMinutesUsedNotifierTime(mContext, System.currentTimeMillis());
	}
	
	public void notifyMinutes() {
		Context context = this.mContext;
		
		int minutesUsed = Settings.getMinutesUsed(context);
		int minutesTotal = Settings.getMinutesPerMonth(context);
		
		// Copy for the notification
		Date billingEndDate = new Date(Settings.getRollOverDateInMillis(context));
		SimpleDateFormat format = new SimpleDateFormat("MMM. d");
		String endDate = format.format(billingEndDate);
		
		String tickerText = "Minutes used: " + minutesUsed + "/" + minutesTotal;
		String expandedTitle = "Minutes used: " + minutesUsed + "/" + minutesTotal;
		String expandedText = (minutesTotal - minutesUsed) + " minutes left until " + endDate;
		
		// The notification, when clicked, should dial the person immediately 
		Intent intent = new Intent(context, MinuteWatch.class); 
		PendingIntent launchIntent = PendingIntent.getActivity(context, 0, intent, 0); 
		
		Notification notification = new Notification(R.drawable.icon, tickerText, System.currentTimeMillis());
		notification.setLatestEventInfo(context, expandedTitle, expandedText, launchIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL; 
		
		notificationManager.notify(NOTIFICATION_ID, notification); 
	}
	
	private void resetAlarm() {
		AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
        PendingIntent notifierIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(MonitorCalls.ACTION_NOTIFY_MINUTE_USAGE), 0);
        alarmManager.set(AlarmManager.RTC, Settings.getNextSpecificHourInMillis(TIME_OF_DAY, mContext), notifierIntent);
	}
}