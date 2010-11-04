package com.extendsandroid.minutewatch.publish;

import com.extendsandroid.minutewatch.publish.database.MinuteDatabaseAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CallConfirm extends Activity {
	public static String PHONE_KEY = "com.extendsandroid.minutewatch.publish.phonekey";
	
	private Button okay;
	private Button cancel;
	private String mPhoneNumber;
	private MinuteDatabaseAdapter db;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm);
        mPhoneNumber = getIntent().getStringExtra(PHONE_KEY);    
        db = new MinuteDatabaseAdapter(this);
        db.open();
        
        okay = (Button) findViewById(R.id.okay);
        okay.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Settings.saveTransmitCall(getBaseContext(), true);
        		makeCall(mPhoneNumber);
        	}
        });
        
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		db.saveDelayedCall(mPhoneNumber);
        		finish();
        	}
        });
        
        setCaughtMessage(mPhoneNumber);
	}
	
	public void makeCall(String phoneNumber) {
		Intent i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
		startActivity(i);
		finish();
	}
	
	public void setCaughtMessage(String phoneNumber) {
	    TextView caughtMessage = (TextView) this.findViewById(R.id.caught); 

		Resources myResources = getResources(); 
	   	String rawOutput = myResources.getString(R.string.call_confirm_output_text); 
	   	String nightTimeMinutesBegin = Settings.getNightTimeBeginsHours(getBaseContext());
	   	int minutesPerMonth = Settings.getMinutesPerMonth(getBaseContext());
	   	int minutesUsed = Settings.getMinutesUsed(getBaseContext());

		String output = String.format(rawOutput, nightTimeMinutesBegin, minutesUsed, minutesPerMonth, nightTimeMinutesBegin);
		
		caughtMessage.setText(output);
	}
	
	@Override 
	public void onDestroy() { 
		db.close(); 
		super.onDestroy(); 
	} 

}
