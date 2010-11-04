package com.extendsandroid.minutewatch.publish;

import com.extendsandroid.minutewatch.publish.services.MonitorCalls;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MonitorCallsManager extends BroadcastReceiver {
	public static final String TAG = "MonitorCallsManager";

	@Override
	public void onReceive(Context context, Intent intent) {
		 if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			  ComponentName comp = new ComponentName(context.getPackageName(), MonitorCalls.class.getName());
			  ComponentName service = context.startService(new Intent().setComponent(comp));

			  if (null == service)
				  Log.e(TAG, "Could not start service " + comp.toString());
		 } else {
			 Log.e(TAG, "Received unexpected intent " + intent.toString());
		 }
	}
}