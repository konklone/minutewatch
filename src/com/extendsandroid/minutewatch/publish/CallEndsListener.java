package com.extendsandroid.minutewatch.publish;

import java.util.Calendar;

import com.extendsandroid.minutewatch.publish.services.MonitorCalls;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallEndsListener extends PhoneStateListener {   
	public static final String TAG = "CallEndsListener";
	
	String mLastIncomingNumber;
	
	long callStarts = 0;
	private Context mContext;
	
	public CallEndsListener(Context mContext) 
	{ 
	     this.mContext = mContext; 
	} 


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
    	
    	long callEnds;
    	switch(state) {
        	case TelephonyManager.CALL_STATE_IDLE:
        		callEnds = android.os.SystemClock.uptimeMillis();
        		if (callStarts != 0L) {
        			int minutesSpentCalling = timeSpentOnCall(callEnds,callStarts);
        			shouldRecordMinutes(minutesSpentCalling);
        		}
        		// clear number stores
        		mLastIncomingNumber = null;
        		MonitorCalls.setLastOutgoingNumber(null);
        		break;
        	case TelephonyManager.CALL_STATE_OFFHOOK:
        		callStarts = android.os.SystemClock.uptimeMillis();
        		break;
        	case TelephonyManager.CALL_STATE_RINGING:
        		mLastIncomingNumber = incomingNumber;
        		callStarts = android.os.SystemClock.uptimeMillis();
        		break;
       		}
    	super.onCallStateChanged(state, incomingNumber);
    }
    
    public int timeSpentOnCall(float ends, float begins){
    	if (begins > ends) {
    		return 0;
    	}
    	int aNumber= (int) Math.ceil((ends - begins) / 60000);
    	return aNumber;
    }
    
    public void shouldRecordMinutes(int minutes) {
    	Boolean hasNightTimeMinutes = Settings.getHasFreeNightTimeMinutes(mContext);
    	Boolean hasWeekendMinutes = Settings.getHasFreeWeekendMinutes(mContext);
    	
    	boolean costMinutes = false; 
    	
    	if	(hasNightTimeMinutes && !(hasWeekendMinutes)) {
    		if (notDuringNightTimeMinutes())
    			costMinutes = true;
    	}
    	else if	(hasWeekendMinutes && !(hasNightTimeMinutes)) {
    		if (isNotTheWeekend())
    			costMinutes = true;
    	}
    	else if	(hasNightTimeMinutes && hasWeekendMinutes) {
    		if (notDuringNightTimeMinutes() && isNotTheWeekend())
    			costMinutes = true;
    	}
    	else // !hasNightTimeMinutes and !hasWeekendMinutes
    		costMinutes = true;
    	
    	// find the number that we were either calling or receiving a call from
    	// and then wipe that number
    	String relevantNumber;
    	String lastOutgoingNumber = MonitorCalls.getLastOutgoingNumber();
    	if (lastOutgoingNumber != null)
    		relevantNumber = lastOutgoingNumber;
    	else
    		relevantNumber = mLastIncomingNumber;
    	
    	if (costMinutes && !isEmergencyNumber(relevantNumber) && !isWhitelistedNumber(relevantNumber))
    		Settings.updateMinutesUsed(mContext, minutes);
    }
    		
    public boolean isNotTheWeekend() {
    	switch(now().get(Calendar.DAY_OF_WEEK)) {
    	case Calendar.SUNDAY:
    		return false;
    	case Calendar.SATURDAY:
    		return false;
    	default: return true ;
    	}
    }
    
    public boolean notDuringNightTimeMinutes() {
    	if (now().get(Calendar.HOUR_OF_DAY) > 5 && now().get(Calendar.HOUR_OF_DAY) < Settings.getNightTimeBeginsValue(mContext)) 
    		return true;
    	else
    		return false;
    }
    
    public boolean isEmergencyNumber(String aNumber) {
    	String[] emergencyNumbers = mContext.getResources().getStringArray(R.array.emergency_numbers);
		return testForNumber(emergencyNumbers, aNumber) ?  true : false;
	}
	
	public boolean isWhitelistedNumber(String aNumber) {
		String[] whitelistedNumbers = MonitorCalls.whitelistedNumbers;
		return testForNumber(whitelistedNumbers, aNumber) ?  true : false;
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
    
    public Calendar now() {
    	Calendar cal = Calendar.getInstance();
    	return cal;
    }
}