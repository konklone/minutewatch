package com.extendsandroid.minutewatch.publish;

import java.util.Calendar;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity { 
	//Option Values
	private static final String OPT_HAS_FREE_NIGHT_TIME_MINUTES = "has_free_night_time_minutes"; 
	private static final boolean OPT_HAS_FREE_NIGHT_TIME_MINUTES_DEF = true;
	private static final String OPT_HAS_FREE_WEEKEND_MINUTES = "has_free_weekend_minutes"; 
	private static final boolean OPT_HAS_FREE_WEEKEND_MINUTES_DEF = true; 
	private static final String OPT_OVERRIDE = "override"; 
	private static final boolean OPT_OVERRIDE_DEF = true; 
	private static final String OPT_NIGHT_TIME_MINUTES_BEGIN = "night_time_minutes_begin";
	private static final String OPT_NIGHT_TIME_MINUTES_BEGIN_DEF = "21"; 
	private static final String OPT_MINUTES_USED = "minutes_used"; 
	private static final int OPT_MINUTES_USED_DEF = 0; 
	private static final String OPT_MINUTES_PER_MONTH = "minutes_per_month";
	private static final int OPT_MINUTES_PER_MONTH_DEF = 300; 
	private static final String OPT_BILL_CYCLE_ENDS = "bill_cycle_ends_array";
	private static final String OPT_BILL_CYCLE_ENDS_DEF = "1";
	private static final String OPT_ROLLOVER_DATE = "rollover_date";
	private static final long OPT_ROLLOVER_DATE_DEF = 2323232323L; 
	private static final String OPT_TRANSMIT_CALL = "transmit_call";
	private static final boolean OPT_TRANSMIT_CALL_DEF = false;
	private static final String OPT_CATCH_BETWEEN = "catch_calls_between";
	private static final String OPT_CATCH_BETWEEN_DEF = "2";
	private static final String OPT_FIRST_TIME = "firstTime";
	private static final boolean OPT_FIRST_TIME_DEF = true;
	private static final String OPT_LAST_USAGE_NOTIFICATION = "last_usage_notification";
	private static final long OPT_LAST_USAGE_NOTIFICATION_DEF = -1;
 
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.settings); 
	}
	
	/** Get the last time (in millis) that the minutes used notifier went off */
	public static long getLastMinutesUsedNotifierTime(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).
			getLong(OPT_LAST_USAGE_NOTIFICATION, OPT_LAST_USAGE_NOTIFICATION_DEF);
	}
	
	/** Get the current value of the has free night time and weekend minutes option */ 
	public static boolean getHasFreeNightTimeMinutes(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(OPT_HAS_FREE_NIGHT_TIME_MINUTES, OPT_HAS_FREE_NIGHT_TIME_MINUTES_DEF); 
	}
	
	/** Get the current value of the has free weekend minutes option */ 
	public static boolean getHasFreeWeekendMinutes(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(OPT_HAS_FREE_WEEKEND_MINUTES, OPT_HAS_FREE_WEEKEND_MINUTES_DEF); 
	}
	
	/** Get the current value of the override option */ 
	public static boolean getOverride(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(OPT_OVERRIDE, OPT_OVERRIDE_DEF); 
	} 
	
	/** Get the current value of the night_time_minutes_begin option */ 
	public static int getNightTimeBeginsValue(Context context) { 
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context) 
			.getString(OPT_NIGHT_TIME_MINUTES_BEGIN, OPT_NIGHT_TIME_MINUTES_BEGIN_DEF)); 
	} 
	
	public static String getNightTimeBeginsHours(Context context) { 
		int timeIndex = getNightTimeBeginsValue(context);
		final String[] time = {"12AM", "1AM", "2AM", "3AM", "4AM", "5AM", "6AM", "7AM",
				"8AM", "9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM",
				"6PM", "7PM", "8PM", "9PM", "10PM", "11PM"};
		return time[timeIndex];
	}
	
	public static long getNextNightTimeInMillis(Context context) {
		int nightTime = Settings.getNightTimeBeginsValue(context);
	    return getNextSpecificHourInMillis(nightTime, context);
	}
	
	public static long getNextSpecificHourInMillis(int hour, Context context) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    
	    long now = System.currentTimeMillis();
	    long nextHour = cal.getTime().getTime();
	    
	    if (now >= nextHour)
	    	nextHour += 1000 * 60 * 60 * 24;
	    
	    return nextHour;
	}
	
	/** Get the current value of the night_time_minutes_begin option */ 
	public static int getMinutesPerMonth(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context) 
			.getInt(OPT_MINUTES_PER_MONTH, OPT_MINUTES_PER_MONTH_DEF); 
	} 
	
	/** Get the current value of the bill_cycle_ends option */ 
	public static String getBillCyleEndDate(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context) 
			.getString(OPT_BILL_CYCLE_ENDS, OPT_BILL_CYCLE_ENDS_DEF); 
	} 
	
	/** Get the current value of used minutes for the month*/ 
	public static int getMinutesUsed(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context) 
			.getInt(OPT_MINUTES_USED, OPT_MINUTES_USED_DEF); 
	} 
	
	/** Get the amount of time to catch calls before night-time minutes begin */ 
	public static int getCatchBetween(Context context) { 
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context) 
			.getString(OPT_CATCH_BETWEEN, OPT_CATCH_BETWEEN_DEF)); 
	}
	
	/** Gets boolean if this the first time the app has been opened */ 
	public static boolean isFirstTime(Context context) { 
		return PreferenceManager.getDefaultSharedPreferences(context) 
			.getBoolean(OPT_FIRST_TIME, OPT_FIRST_TIME_DEF); 
	} 
	
	public static void setLastMinutesUsedNotifierTime(Context context, long newTime) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putLong(OPT_LAST_USAGE_NOTIFICATION, newTime).commit();
	}
	
	public static void updateMinutesUsed(Context context, int valueToBeAdded){  // Retrieve an editor to modify the shared preferences. 
		int oldValue = getMinutesUsed(context);
		int updatedValue = oldValue + valueToBeAdded;
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		editor.putInt(OPT_MINUTES_USED, updatedValue).commit();
	}
	
	public static void resetMinutesUsed(Context context){  // Retrieve an editor to modify the shared preferences. 
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		editor.putInt(OPT_MINUTES_USED, 0).commit();
	}

	
	
	public static void saveTransmitCall(Context context, boolean v){  // Retrieve an editor to modify the shared preferences. 
		 Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		 editor.putBoolean(OPT_TRANSMIT_CALL, v).commit();
	}
	 
	public static boolean getTransmitCall(Context context) {
		 return PreferenceManager.getDefaultSharedPreferences(context) 
			.getBoolean(OPT_TRANSMIT_CALL, OPT_TRANSMIT_CALL_DEF); 
	}
	
	/** Get the current RollOver Date*/ 
	public static Calendar getRollOverDatePreference(Context context) { 
		Calendar rolloverDate =  Calendar.getInstance(); 
		rolloverDate.setTimeInMillis(getRollOverDateInMillis(context));
		return rolloverDate;
	}
	
	/** Get the current rollover date in raw milliseconds */
	public static long getRollOverDateInMillis(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).
			getLong(OPT_ROLLOVER_DATE, OPT_ROLLOVER_DATE_DEF);
	}
	
	/** Set the reset user inputed date to match new roll-over date*/ 
	public static void setBillCycleEndsDate(Context context, long date) {  // Retrieve an editor to modify the shared preferences. 
		 Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		 Calendar cal = Calendar.getInstance();
		 cal.setTimeInMillis(date);
		 String dayOfMonth = "" + cal.get(Calendar.DAY_OF_MONTH);
		 editor.putString(OPT_BILL_CYCLE_ENDS, dayOfMonth).commit();
	}
	
	/** Used in the CreateBillCycleEndDate class to set the user inputed date*/ 
	public static void setBillCycleEndsDateToOne(Context context){  // Retrieve an editor to modify the shared preferences. 
		 Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		 editor.putString(OPT_BILL_CYCLE_ENDS, "1").commit();
	}
	
	public static void setRollOverDatePreference(Context context, long date){  // Retrieve an editor to modify the shared preferences. 
		 Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		 editor.putLong(OPT_ROLLOVER_DATE, date).commit();
		 setBillCycleEndsDate(context, date);
	}
	
	public static void appHasBeenOpen(Context context){  // Retrieve an editor to modify the shared preferences. 
		 Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		 editor.putBoolean(OPT_FIRST_TIME, false).commit();
	}
} 