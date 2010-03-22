package com.extendsandroid.minutewatch.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Welcome extends Activity {
	
	private Button okay;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        okay = (Button) findViewById(R.id.welcome_okay);
        okay.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		startActivity(new Intent(getBaseContext(), Settings.class));
        		Settings.appHasBeenOpen(getBaseContext());
        		finish();
        	}
        });
	}
	
}