package com.extendsandroid.minutewatch.publish.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.TextView;

import com.extendsandroid.minutewatch.publish.R;


public class UnderlinedTextView extends TextView {

	Paint linePaint;
	int mWhite;
	int mGrey;
	
	public UnderlinedTextView (Context context, AttributeSet ats, int defStyle) { 
	    super(context, ats, defStyle); 
	  } 
	 
	  public UnderlinedTextView (Context context) { 
	    super(context); 
	    
	  } 
	  public UnderlinedTextView (Context context, AttributeSet attrs) { 
	    super(context, attrs); 
	    init();
	  } 
	  
	  @Override
	  public void onDraw(Canvas canvas) { 
		  int height = getMeasuredHeight();;
		  int width = getMeasuredWidth();
		  int white = mWhite;
		  int grey = mGrey;
		  linePaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		  linePaint.setShader(new LinearGradient(0, height, width + 50, height, white, grey, TileMode.CLAMP));
		  canvas.drawLine(0, height, width, height, 
		                  linePaint); 
		  canvas.save(); 
		  super.onDraw(canvas); 
		  canvas.restore(); 
	  } 
	  
	  private void init() {
	
		  // Get a reference to our resource table. 
		  Resources myResources = getResources();
		  this.mWhite = myResources.getColor(R.color.white);
		  this.mGrey = myResources.getColor(R.color.grey);
	  } 


}
