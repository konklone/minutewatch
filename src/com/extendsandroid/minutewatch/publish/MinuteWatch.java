package com.extendsandroid.minutewatch.publish;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MinuteWatch extends Activity {
	
	Context mContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	return;
//        super.onCreate(savedInstanceState);
//        
//        this.mContext = getBaseContext();
//        setContentView(R.layout.main);
//        
//        startService(new Intent("com.extendsandroid.minutewatch.publish.SERVICE_MONITOR_CALLS"));
//        
//        boolean firstTime = Settings.isFirstTime(mContext); 
//        if (firstTime) {
//    		startActivity(new Intent(this, Welcome.class));
//    		Settings.setLastMinutesUsedNotifierTime(mContext, System.currentTimeMillis());
//        }
//        else {
//	        setTextViews();
//        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if (!(Settings.isFirstTime(getBaseContext()))) {
    		setTextViews();
    	}
    }
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) { 
	    super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater(); 
	    inflater.inflate(R.menu.minutewatch, menu);
	    return true;
    } 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) { 
    	case R.id.settings: 
    		startActivity(new Intent(this, Settings.class)); 
    		return true; 
    	case R.id.whitelist: 
    		startActivity(new Intent(this, Whitelist.class)); 
    		return true; 
    	}
    	return true;
    }
	
	public static String showDate(Calendar date) {
		final String[] monthName = {"January", "February",
		      "March", "April", "May", "June", "July",
		      "August", "September", "October", "November",
		      "December"};
		int Month = date.get(Calendar.MONTH);
		int Day = date.get(Calendar.DAY_OF_MONTH);
		String returnString = monthName[Month] + " " + Day;
		return returnString;
	}
	
	public void setTextViews() {
		Context context = mContext;
		int minutesUsed = Settings.getMinutesUsed(context);
		int minutesPerMonth= Settings.getMinutesPerMonth(context);
		Boolean hasFreeNightTimeMinutes = Settings.getHasFreeNightTimeMinutes(context);
		Boolean hasFreeWeekendMinutes = Settings.getHasFreeWeekendMinutes(context);
		Boolean hasOvverride = Settings.getOverride(context);
		String planType = "";
		
		final String[] time = {"12AM", "1AM", "2AM", "3AM", "4AM", "5AM", "6AM", "7AM",
				"8AM", "9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM",
				"6PM", "7PM", "8PM", "9PM", "10PM", "11PM"};
		int nightTimeMinutesBeginsIndex = Settings.getNightTimeBeginsValue(context);
		int catchTimeBeginsIndex = nightTimeMinutesBeginsIndex - Settings.getCatchBetween(context);
		String catchTimeBegins = time[catchTimeBeginsIndex];
		String nightTimeMinutesBegins = time[nightTimeMinutesBeginsIndex];
		
	    TextView minutesUsedTextView = (TextView) this.findViewById(R.id.minutes_used); 
	    minutesUsedTextView.setText(minutesUsed + " / " + minutesPerMonth);

	    TextView endOfMonthView = (TextView) this.findViewById(R.id.end_of_billing_month); 
	    endOfMonthView.setText(showDate(Settings.getRollOverDatePreference(context)));
	    
	    TextView planView = (TextView) this.findViewById(R.id.plan_type); 
	    
	    if (hasFreeNightTimeMinutes && !(hasFreeWeekendMinutes))
	    	planType = "Unlimited Nights";
	    else if (hasFreeWeekendMinutes && !(hasFreeNightTimeMinutes))
	    	planType = "Unlimited Weekends";
	    else if (hasFreeWeekendMinutes && hasFreeNightTimeMinutes)
	    	planType = "Unlimited Nights & Weekends";
	    else 
	    	planType = "Regular";
	    planView.setText(planType);
	   
	    TextView catchCallsView = (TextView) this.findViewById(R.id.catch_calls);
	    TextView catchCallsLableView = (TextView) this.findViewById(R.id.catch_calls_label); 

	    catchCallsView.setText("Between " + catchTimeBegins + " and " +  nightTimeMinutesBegins);
	    
	    if ( (!(hasFreeNightTimeMinutes)) && (!(hasFreeWeekendMinutes)) || (!(hasOvverride)) ) {
		    catchCallsView.setVisibility(View.GONE);
		    catchCallsLableView.setVisibility(View.GONE);
	    }
		else {
			catchCallsView.setVisibility(View.VISIBLE);
	    	catchCallsLableView.setVisibility(View.VISIBLE);
	    }
	}
}