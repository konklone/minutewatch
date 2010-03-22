package com.extendsandroid.minutewatch.publish;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ContactPicker extends Activity {

	SimpleCursorAdapter myAdapter;
	int[] toLayoutIDs = new int[] { R.id.name, R.id.contactnumber};
	String[] fromColumns = new String[] {People.NAME, People.NUMBER}; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listcontactpicker);
		final String uriString = "content://contacts/people/";
		final Cursor myCursor = managedQuery(Uri.parse(uriString), null, "primary_phone IS NOT NULL", null, "name ASC");
		myAdapter = new SimpleCursorAdapter(this, 
                R.layout.contactpickerlayout, 
                myCursor, 
                fromColumns, 
                toLayoutIDs); 
		ListView lv = (ListView)findViewById(R.id.contactListView);

		lv.setAdapter(myAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() { 
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Move the cursor to the selected item 
				myCursor.moveToPosition(position); 
				// Extract the row id. 
				int rowId = myCursor.getInt(myCursor.getColumnIndexOrThrow("_id")); 
				// Construct the result URI. 
				Uri outURI = Uri.parse( "content://contacts/people/".toString() + rowId); 
				Intent outData = new Intent(); 
				outData.setData(outURI); 
				setResult(Activity.RESULT_OK, outData); 
				finish(); 
			} 
		  }); 
	}
}