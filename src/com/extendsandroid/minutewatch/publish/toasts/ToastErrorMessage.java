package com.extendsandroid.minutewatch.publish.toasts;

import com.extendsandroid.minutewatch.publish.view.ErrorView;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

public class ToastErrorMessage extends Toast {
	
	public ToastErrorMessage(Context context) {
		super(context);
	}
	
	public static Toast makeText(Context context, CharSequence text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		ErrorView lay = new ErrorView(context); 
		TextView textView = new TextView(context);
		textView.setText(text);
		lay.addView(textView); 
		toast.setView(lay);
        return toast;
    }
}