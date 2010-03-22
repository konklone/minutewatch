package com.extendsandroid.minutewatch.publish;

import java.util.Calendar;

import com.extendsandroid.minutewatch.publish.toasts.ToastErrorMessage;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class BillCycle {
	
	private static String[] mMonthName = {"January", "February",
        "March", "April", "May", "June", "July",
        "August", "September", "October", "November",
        "December"};

	/** Updates the billing cycle end date, if we're past the current one */
	public static void updateBillCycleEndDate(Context context) {
		Boolean sendToast = false;
		
		long now = System.currentTimeMillis();
		long cycleEnds = Settings.getRollOverDateInMillis(context);
		
		// If we're past the billing date, we want to set it to 11:59pm on the next billing date in the future
		if (now > cycleEnds) {
			// get the current billing end day
			int currentEndBillDay = Integer.parseInt(Settings.getBillCyleEndDate(context));
			
			// Get the exact time of the next rollover time
			long nextDate = nextBillingDate(context, currentEndBillDay, sendToast);
			
			// Set the billing end day
			Settings.setBillCycleEndsDate(context, nextDate);
			
			// Set the rollover time
			Settings.setRollOverDatePreference(context, nextDate);
			
			//Reset Minutes
			Settings.resetMinutesUsed(context);
		}
	}
	
	public static long nextBillingDate(Context context, int billingDay, Boolean sendToast) {
		// first, figure out the month - if we're past the date, it's this month, otherwise next month
		Calendar today = Calendar.getInstance();
		
		int todaysDay = today.get(Calendar.DAY_OF_MONTH);
		int todaysMonth = today.get(Calendar.MONTH);
		
		int billingMonth = todaysMonth;
		
		if (todaysDay > billingDay) {
			billingMonth += 1;
		}
		
		// if that month contains the day, then we're done
		// otherwise, it's the 1st of the month after that one
		Calendar billingCalendar = Calendar.getInstance();
		billingCalendar.set(Calendar.DAY_OF_MONTH, 1); // just for now, to prevent rollover
		billingCalendar.set(Calendar.MONTH, billingMonth);
		
		if (billingCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) < billingDay) {
			if (sendToast == true) {
				dateResetMessage(billingMonth, billingMonth + 1, billingDay, context);
				Settings.setBillCycleEndsDateToOne(context);
			}
			billingDay = 1;
			billingMonth += 1;
		}
		
		billingCalendar.set(Calendar.MONTH, billingMonth);
		billingCalendar.set(Calendar.DAY_OF_MONTH, billingDay);
		billingCalendar.set(Calendar.HOUR_OF_DAY, 23);
		billingCalendar.set(Calendar.MINUTE, 59);
		
		return billingCalendar.getTimeInMillis();
	}
	
	public static void dateResetMessage(int triedMonthIndex, int trueMonthIndex,  int dateValue, Context context) {
		String[] monthName = mMonthName;
		String triedMonth = monthName[triedMonthIndex];
		String trueMonth = monthName[trueMonthIndex];
		int duration = Toast.LENGTH_LONG; 
		String resetToastMessage = "%s does not have %d days, so your billing cycle will end on the first of %s. "; 
		String output = String.format(resetToastMessage, triedMonth, dateValue, trueMonth);
		
		Toast toast = ToastErrorMessage.makeText(context, output, duration); 
		toast.setGravity(Gravity.BOTTOM, 0, 25);
		toast.show();
	}

}