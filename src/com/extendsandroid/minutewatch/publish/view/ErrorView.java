package com.extendsandroid.minutewatch.publish.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ErrorView extends LinearLayout {
	
private Paint innerPaint, borderPaint ;
    
	public  ErrorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public  ErrorView(Context context) {
		super(context);
		init();
    	setPadding(15, 5, 15, 5);
	}

	private void init() {
		innerPaint = new Paint();
		innerPaint.setARGB(255, 75, 00, 00); //gray
		innerPaint.setAntiAlias(true);

		borderPaint = new Paint();
		borderPaint.setARGB(255, 150, 00, 30);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
	}
	
	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}

	public void setBorderPaint(int aColor) {
		this.borderPaint.setColor(aColor);
	}

    @Override
    protected void dispatchDraw(Canvas canvas) {
    	
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRoundRect(drawRect, 10, 10, innerPaint);
		canvas.drawRoundRect(drawRect, 10, 10, borderPaint);
		
		super.dispatchDraw(canvas);
    }
	
}