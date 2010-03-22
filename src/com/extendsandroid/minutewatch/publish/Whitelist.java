package com.extendsandroid.minutewatch.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.extendsandroid.minutewatch.publish.database.MinuteDatabaseAdapter;
import com.extendsandroid.minutewatch.publish.services.MonitorCalls;
import com.extendsandroid.minutewatch.publish.toasts.ToastErrorMessage;

public class Whitelist extends Activity {
	
	static final private int ADD_NEW_NUMBER = R.id.add_whitelist_number;
	static final private int REMOVE_NUMBER  = R.id.remove_whitelisted_number;
	static final private int PICK_FROM_CONTACT  = R.id.pickFromContacts;
	
	static final private int NO = Menu.FIRST + 2;
	private static final int PICK_CONTACT = 6534;
	
	private Cursor myCursor;
	private SimpleCursorAdapter myAdapter;
	private MinuteDatabaseAdapter dBAdapter;
	private int[] toLayoutIDs = new int[] { R.id.name, R.id.contactnumber};
	private String[] fromColumns = new String[] {MinuteDatabaseAdapter.KEY_NAME, MinuteDatabaseAdapter.KEY_WHITELISTED_NUMBER};
	private ListView whitelistedNumbersList;
	private EditText input; 
	private Button submit;
	private boolean addingNew = false; 
	private TextView noNumbersAdded;

// =========================================================== 
// Application Methods 
// ===========================================================
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whitelist);
		dBAdapter = new MinuteDatabaseAdapter(this);
	    dBAdapter.open();
		myCursor = dBAdapter.fetchAllWhitelistedNumbers();
		startManagingCursor(myCursor);
		myAdapter = new SimpleCursorAdapter(this, 
                R.layout.contactpickerlayout, 
                myCursor, 
                fromColumns, 
                toLayoutIDs); 
		whitelistedNumbersList = (ListView)findViewById(R.id.whitelistedNumbersList);
		
		input = (EditText)findViewById(R.id.whitelistInput);
		submit = (Button) findViewById(R.id.whitelistSubmit);
		noNumbersAdded = (TextView)findViewById(R.id.no_numbers_added);
		whitelistedNumbersList.setAdapter(myAdapter);
		input.setOnKeyListener(new OnKeyListener() { 
	        public boolean onKey(View v, int keyCode, KeyEvent event) { 
	          if (event.getAction() == KeyEvent.ACTION_DOWN) {
	            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)  { 
	            	processNewNumber();
	            	return true;
	            }
	          }
	          return false; 
	        } 
	    });
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				processNewNumber();
			}
		});
		
		
		registerForContextMenu(whitelistedNumbersList);
		showNoListView();
	}
	
	public void processNewNumber() {
		String aNumber = input.getText().toString();
    	if (aNumber.length() < 3)
    		sendTooShortMessage(aNumber);
    	else if (!(numberIsUnique(aNumber))) 
    		sendNotUniqueMessage(aNumber);
    	else {
        	WhitelistedNumberItem newNumber;
    		newNumber = new WhitelistedNumberItem(aNumber);
            dBAdapter.createNewWhitelistedNumber(newNumber);
            myCursor.requery();
            showNoListView(); 
            myAdapter.notifyDataSetChanged();
            MonitorCalls.buildWhitelistedNumberArray();
            cancelAdd();
    	}
	}
	
	public void showNoListView() {
		if (myCursor.getCount() == 0) 
			noNumbersAdded.setVisibility(View.VISIBLE);
		else
			noNumbersAdded.setVisibility(View.GONE);
	}

	@Override 
	public void onDestroy() { 
		// Close the database 
		dBAdapter.close(); 
		super.onDestroy(); 
	} 	
	
// =========================================================== 
// Menu Methods 
// ===========================================================
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) { 
	  super.onCreateOptionsMenu(menu); 
	  // Create and add new menu items. 
	  MenuInflater inflater = getMenuInflater(); 
	  inflater.inflate(R.menu.whitelist, menu);
	  return true; 
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item); 
		long index = whitelistedNumbersList.getSelectedItemId(); 
		switch(item.getItemId()) { 
    	case (REMOVE_NUMBER): {
    		if (addingNew) 
    	    	cancelAdd();
    	    else
    	    	removeItem(index);
    		return true; 
    		}
    	case (PICK_FROM_CONTACT):
    		 startActivityForResult(new Intent(this, ContactPicker.class), PICK_CONTACT); 
    		 return true; 
    	case (ADD_NEW_NUMBER): { 
    	      addNewItem(); 
    	      return true; 
    	    } 
    	}
    	return false;
    }
		
	@Override 
	public boolean onPrepareOptionsMenu(Menu menu) { 
	  int idx = whitelistedNumbersList.getSelectedItemPosition(); 
	  String removeTitle = getString(addingNew ? 
	                                 R.string.cancel_input : R.string.remove_whitelisted_number_label); 
	  MenuItem removeItem = menu.findItem(REMOVE_NUMBER);
	  MenuItem pickFromtContactItem = menu.findItem(PICK_FROM_CONTACT); 
	  removeItem.setTitle(removeTitle); 
	  removeItem.setVisible(addingNew || idx > -1);
	  removeItem.setEnabled(addingNew || idx > -1);
	  pickFromtContactItem.setVisible(!(addingNew));
	  pickFromtContactItem.setEnabled(!(addingNew));

	  return true; 
	} 
	
	private void updateArray() { 
		myCursor.requery();
		myAdapter.notifyDataSetChanged(); 
        showNoListView(); 
	} 
	  
	private void cancelAdd() { 
		addingNew = false; 
		input.setVisibility(View.GONE);
		submit.setVisibility(View.GONE);
        showNoListView(); 
	} 
	
	private void addNewItem() { 
		addingNew = true; 
		input.setVisibility(View.VISIBLE); 
		submit.setVisibility(View.VISIBLE);
		input.requestFocus(); 
		noNumbersAdded.setVisibility(View.GONE);
	} 
	private void removeItem(long _index) { 
		dBAdapter.deleteWhitelistedNumber(_index); 
		updateArray();
        MonitorCalls.buildWhitelistedNumberArray(); 
	} 

// =========================================================== 
// Context Menu Methods 
// ===========================================================
	
	@Override 
	public boolean onContextItemSelected(MenuItem item) {  
	  super.onContextItemSelected(item); 
	  switch (item.getItemId()) { 
	    case (REMOVE_NUMBER): { 
	      AdapterView.AdapterContextMenuInfo menuInfo; 
	      menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo(); 
	      long index = menuInfo.id; 
	      removeItem(index); 
	      return true; 
	    } 
	  } 
	  return false; 
	}
	
	@Override 
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) { 
	  super.onCreateContextMenu(menu, v, menuInfo); 
	  menu.setHeaderTitle("Remove Number?"); 
	  menu.add(0, REMOVE_NUMBER, Menu.NONE, "Yes"); 
	  menu.add(0, NO, Menu.NONE, "No"); 

	} 
	
// =========================================================== 
// Error Message Methods 
// ===========================================================
	
	public boolean numberIsUnique(String aNumber) {
		aNumber = aNumber.replaceAll("\\W", "");
		String[] whitelistedNumbersArray = MonitorCalls.whitelistedNumbers;
		int i;
		for (i=0; i < whitelistedNumbersArray.length; i++) // for each element,
			if(whitelistedNumbersArray[i].equals(aNumber)) // if it finds the item?
				break;
	    if (i == whitelistedNumbersArray.length) // It has not found the number because the loop ran to completion
	    	return true; 
	    else
	    	return false;
    }
	
	public void sendNotUniqueMessage(String aNumber) {
		Context context = getBaseContext();
		int duration = Toast.LENGTH_LONG;
		Resources myResources = getResources(); 
		String message = myResources.getString(R.string.nonUniqueNumber);
		String output = String.format(message, aNumber);
		Toast toast = ToastErrorMessage.makeText(context, output, duration);
		toast.setGravity(Gravity.BOTTOM, 0, 25);
		toast.show();
	}
	
	public void sendTooShortMessage(String aNumber) {
		Context context = getBaseContext();
		int duration = Toast.LENGTH_SHORT;
		String output = "Number should be at least 3 digits.";
		Toast toast = ToastErrorMessage.makeText(context, output, duration); 
		toast.setGravity(Gravity.BOTTOM, 0, 25);
		toast.show();
	}

// =========================================================== 
// Activity Result Methods 
// ===========================================================
	
	@Override 
    public void onActivityResult(int reqCode, int resCode, Intent data) { 
      super.onActivityResult(reqCode, resCode, data); 
      switch(reqCode) { 
        case (PICK_CONTACT) : { 
          if (resCode == Activity.RESULT_OK) { 
            Uri contactData = data.getData(); 
            Cursor c = managedQuery(contactData, null, null, null, null); 
            c.moveToFirst(); 
            String aNumber; 
            aNumber = c.getString(c.getColumnIndexOrThrow(People.NUMBER));
            if (numberIsUnique(aNumber)) {
                String aName; 
	            aName = c.getString(c.getColumnIndexOrThrow(People.NAME)); 
	            WhitelistedNumberItem newNumber ;
	        	newNumber = new WhitelistedNumberItem(aNumber, aName); 
	            dBAdapter.createNewWhitelistedNumber(newNumber);
	            updateArray();
	            myAdapter.notifyDataSetChanged();
	            MonitorCalls.buildWhitelistedNumberArray();
            }
            else
            	sendNotUniqueMessage(aNumber);
          } 
          break; 
        } 
      } 
    } 

}