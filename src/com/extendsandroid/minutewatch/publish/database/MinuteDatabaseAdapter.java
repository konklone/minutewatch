package com.extendsandroid.minutewatch.publish.database;

import com.extendsandroid.minutewatch.publish.WhitelistedNumberItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Simple database access helper class. Defines the basic CRUD operations,
 * and gives the ability to list all rows as well as retrieve or modify a specific row.
 */
public class MinuteDatabaseAdapter {
	
    private final DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    public static final String KEY_ID = "_id";    

    /** App-specific parameters
     * 
     */
    private static final String DATABASE_TABLE_DELAYED_CALLS = "phone_calls";
    private static final String DATABASE_TABLE_WHITELISTED_NUMBERS = "whitelisted_numbers";
    private static final String DATABASE_NAME = "minutewatch.db";
    private static final int DATABASE_VERSION = 1;
    
    /** DELAYED CALLS TABLE */
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final int PHONE_NUMBER_COLUMN = 1;
    public static final String KEY_TIME_CALLED = "time_called";
    public static final int  TIME_CALLED_COLUMN = 2;
    private String[] delayedCallsFields = new String[] {KEY_ID, KEY_PHONE_NUMBER, KEY_TIME_CALLED}; 

    
    /** WHITELISTED NUMBERS TABLE */
    public static final String KEY_WHITELISTED_NUMBER = "whitelisted_number";
    public static final int WHITELISTED_NUMBER_COLUMN = 1;
    public static final String KEY_NAME = "name";
    public static final int  NAME_COLUMN = 2;
    public static final String KEY_CREATED_AT = "created_at";
    public static final int  CREATED_AT_COLUMN = 3;

    private String[] whitelistedNumbersFields = new String[] {KEY_ID, KEY_WHITELISTED_NUMBER, KEY_NAME, KEY_CREATED_AT}; 
  
    // public static final String KEY_STARTUP = "on_startup";
    
    private static final String CREATE_TABLE_DELAYED_CALLS =
            "create table " + DATABASE_TABLE_DELAYED_CALLS + " (" +
                KEY_ID + " integer primary key autoincrement, " +
                KEY_PHONE_NUMBER+ " string not null, " +
                KEY_TIME_CALLED + " long not null);";
    private static final String CREATE_TABLE_WHITELISTED_NUMBERS =
             "create table " + DATABASE_TABLE_WHITELISTED_NUMBERS + " (" +
                KEY_ID + " integer primary key autoincrement, " +
                KEY_WHITELISTED_NUMBER + " string not null, " +
                KEY_NAME + " string not null, " +
                KEY_CREATED_AT + " long nut null);";
   
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    
    public MinuteDatabaseAdapter(Context mContext) { 
        this.mDbHelper = new DatabaseHelper(mContext); 
      } 


    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public void open() throws SQLException {
    	try { 
    			mDb = mDbHelper.getWritableDatabase(); 
    		} catch (SQLiteException ex) { 
    			mDb = mDbHelper.getReadableDatabase(); 
    		} 
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new row using the information provided. If the row is
     * successfully created, return the new rowId for that row, otherwise, return
     * a -1 to indicate failure.
     * 
     * @param args the column keys and data of the row to be created
     * @return rowId or -1 if failed
     */

    public long saveDelayedCall(String aPhoneNumber) {
    	ContentValues args = new ContentValues();
    	long now = System.currentTimeMillis();
    	args.put(KEY_TIME_CALLED, now);
    	args.put(KEY_PHONE_NUMBER, aPhoneNumber);
        return mDb.insert(DATABASE_TABLE_DELAYED_CALLS, null, args);
    }
    
    public long createNewWhitelistedNumber(WhitelistedNumberItem whitelistedNumberItem) {
    	ContentValues args = new ContentValues();
    	long now = System.currentTimeMillis();
    	args.put(KEY_CREATED_AT, now);
    	args.put(KEY_WHITELISTED_NUMBER, whitelistedNumberItem.getNumber());
    	args.put(KEY_NAME, whitelistedNumberItem.getName());
        return mDb.insert(DATABASE_TABLE_WHITELISTED_NUMBERS, null, args);
    }
    
    /**
     * Truncate the delayed calls table. 
     * 
     */
    public boolean truncateDelayedCallsTable() {
    	return mDb.delete(DATABASE_TABLE_DELAYED_CALLS, "1", null) > 0;
    }

    /**
     * Delete the row with the given rowId
     * 
     * @param rowId id of row to delete
     * @return true if deleted, false otherwise
     */
    
    public boolean deleteWhitelistedNumber(long rowId) {
        return mDb.delete(DATABASE_TABLE_WHITELISTED_NUMBERS, KEY_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all rows in the database
     * 
     * @return Cursor over all rows
     */
    public Cursor fetchAllDelayedCalls() {
        return mDb.query(DATABASE_TABLE_DELAYED_CALLS, delayedCallsFields,
        		null, null, null, null, null);
    }
    
    public Cursor fetchAllWhitelistedNumbers() {
        return mDb.query(DATABASE_TABLE_WHITELISTED_NUMBERS, whitelistedNumbersFields,
        		null, null, null, null,  KEY_ID + " DESC");
    }

    /**
     * Return a Cursor positioned at the row that matches the given rowId
     * 
     * @param rowId ID of row to retrieve
     * @return Cursor positioned to matching row, if found
     * @throws SQLException if row could not be found/retrieved
     */
    
    public Cursor fetchWhitelistedNumber(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(DATABASE_TABLE_WHITELISTED_NUMBERS,  whitelistedNumbersFields, KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        return mCursor;
    }

    /**
     * Update the row using the details provided. The row to be updated is
     * specified using the rowId, and it is altered to use the values passed in 
     * 
     * @param rowId id of row to update
     * @param args set of column ids and values
     * @return true if the row was successfully updated, false otherwise
     */
    public boolean updateRow(long rowId, ContentValues args) {
        return mDb.update(DATABASE_TABLE_DELAYED_CALLS, args, KEY_ID + "=" + rowId, null) > 0;
    }
    
    public boolean updateWhitelistedNumber(long rowId, ContentValues args) {
        return mDb.update(DATABASE_TABLE_WHITELISTED_NUMBERS, args, KEY_ID + "=" + rowId, null) > 0;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_DELAYED_CALLS);
            db.execSQL(CREATE_TABLE_WHITELISTED_NUMBERS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MinuteDatabaseAdapter.DATABASE_TABLE_WHITELISTED_NUMBERS);
            db.execSQL("DROP TABLE IF EXISTS " + MinuteDatabaseAdapter.DATABASE_TABLE_DELAYED_CALLS);
            onCreate(db);
        }
    }

    
}
