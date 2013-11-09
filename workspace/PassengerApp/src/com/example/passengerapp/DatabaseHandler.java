package com.example.passengerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "storedTickets";
	
	private static final String TABLE_TICKETS = "tickets";
	
	private static final String KEY_USERID = "user_id";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TIME = "time";
    private static final String KEY_SIGNATURE = "signature";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TICKETS_TABLE = "CREATE TABLE " + TABLE_TICKETS + "("
                + KEY_USERID + " INTEGER," + KEY_DEVICE + " TEXT,"
                + KEY_TYPE + " INTEGER," + KEY_TIME + " INTEGER PRIMARY KEY, " + KEY_SIGNATURE + " TEXT" +")";
        db.execSQL(CREATE_TICKETS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
		 
        // Create tables again
        onCreate(db);
	}
	
	public void addTicket(Ticket t){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_USERID, t.getUserID());
		values.put(KEY_DEVICE, t.getDeviceID());
		values.put(KEY_TYPE, t.getType());
		values.put(KEY_TIME, t.getTime());
		values.put(KEY_SIGNATURE, t.getSignature());
		
		 db.insert(TABLE_TICKETS, null, values);
		 db.close();
	}

}
