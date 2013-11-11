package pt.fe.up.cmov.busticket.client;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "storedTickets";
	
	private static final String TABLE_TICKETS = "tickets";
	private static final String TABLE_VALIDATIONS = "validations";
	
	private static final String KEY_USERID = "user_id";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TIME = "time";
    private static final String KEY_SIGNATURE = "signature";
    
    private static final String KEY_VALIDATION_TIME = "validation_time";
    private static final String KEY_BUSID = "bus_id";
    private static final String KEY_BUS_SIGNATURE = "bus_signature";
    
   
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TICKETS_TABLE = "CREATE TABLE " + TABLE_TICKETS + "("
                + KEY_USERID + " INTEGER,"
				+ KEY_DEVICE + " TEXT,"
                + KEY_TYPE + " INTEGER,"
				+ KEY_TIME + " INTEGER PRIMARY KEY, "
                + KEY_SIGNATURE + " TEXT" +")";
		
		String CREATE_VALIDATIONS_TABLE = "CREATE TABLE " + TABLE_VALIDATIONS + "("
				+ KEY_TIME + " INTEGER, "
				+ KEY_VALIDATION_TIME + " INTEGER PRIMARY KEY, "
				+ KEY_BUSID + " TEXT, "
				+ KEY_BUS_SIGNATURE + " TEXT, "
				+ "FOREIGN KEY ("+ KEY_TIME + ") REFERENCES "+ TABLE_TICKETS + " ("+ KEY_TIME + "))";
        db.execSQL(CREATE_TICKETS_TABLE);
        db.execSQL(CREATE_VALIDATIONS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALIDATIONS);
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

	public void addValidation(Validation v){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TIME, v.getTime());
		values.put(KEY_VALIDATION_TIME, v.getValidationTime());
		values.put(KEY_BUS_SIGNATURE, v.getBusSignature());
		values.put(KEY_BUSID, v.getBusID());
		
		db.insert(TABLE_VALIDATIONS, null, values);
		db.close();
	}
	
	
	public List<Ticket> getAllTickets(){
		List<Ticket> ticketList = new ArrayList<Ticket>();
		
		String query = "SELECT * FROM " + TABLE_TICKETS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(query, null);
		
		if (c.moveToFirst()) {
	        do {
	            Ticket ticket = new Ticket();
	            ticket.setUserID(Integer.parseInt(c.getString(0)));
	            ticket.setDeviceID(c.getString(1));
	            ticket.setType(Integer.parseInt(c.getString(2)));
	            ticket.setTime(Long.parseLong(c.getString(3)));
	            ticket.setSignature(c.getString(4));
	            ticketList.add(ticket);
	        } while (c.moveToNext());
	    }
		
		return ticketList;
	}
	
	public List<Ticket> getNotValidatedTickets(){
		List<Ticket> ticketList = new ArrayList<Ticket>();
		
		String query = "SELECT * FROM " + TABLE_TICKETS + " t WHERE t.time NOT IN (SELECT time FROM " + TABLE_VALIDATIONS + ")";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(query, null);
		
		if (c.moveToFirst()) {
	        do {
	            Ticket ticket = new Ticket();
	            ticket.setUserID(Integer.parseInt(c.getString(0)));
	            ticket.setDeviceID(c.getString(1));
	            ticket.setType(Integer.parseInt(c.getString(2)));
	            ticket.setTime(Long.parseLong(c.getString(3)));
	            ticket.setSignature(c.getString(4));
	            ticketList.add(ticket);
	        } while (c.moveToNext());
	    }
		
		return ticketList;
	}
	
	public int[] getNotValidatedTicketsCount(){
		int[] ticketCount = new int[3];
		
		String query1 = "SELECT * FROM " + TABLE_TICKETS + " t WHERE t.time NOT IN (SELECT time FROM " + TABLE_VALIDATIONS + ") AND t.type = 1";
		String query2 = "SELECT * FROM " + TABLE_TICKETS + " t WHERE t.time NOT IN (SELECT time FROM " + TABLE_VALIDATIONS + ") AND t.type = 2";
		String query3 = "SELECT * FROM " + TABLE_TICKETS + " t WHERE t.time NOT IN (SELECT time FROM " + TABLE_VALIDATIONS + ") AND t.type = 3";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(query1, null);
		Cursor c2 = db.rawQuery(query2, null);
		Cursor c3 = db.rawQuery(query3, null);
		
		
		ticketCount[0] = c.getCount();
		ticketCount[1] = c2.getCount();
		ticketCount[2] = c3.getCount();
		return ticketCount;
	}
	
	public JSONObject getOldestTicket(int i, Context appC){
		JSONObject obj=new JSONObject();
		String query="SELECT * FROM "+ TABLE_TICKETS + " t WHERE t.time NOT IN (SELECT time FROM " + TABLE_VALIDATIONS + ") AND t.type = ? order by "+KEY_TIME+" asc";
		SQLiteDatabase db= this.getReadableDatabase();
		Cursor c=db.rawQuery(query, (new String[]{Integer.toString(i)}));
		c.moveToNext();
		c.moveToFirst();
		if(c.isFirst()){
			try {
				obj.put("user", c.getString(c.getColumnIndex(KEY_USERID)));
				obj.put("type", c.getString(c.getColumnIndex(KEY_TYPE)));
				obj.put("time", c.getString(c.getColumnIndex(KEY_TIME)));
				obj.put("device", c.getString(c.getColumnIndex(KEY_DEVICE)));
				if(appC!=null){
					if(!c.getString(c.getColumnIndex(KEY_DEVICE)).equals(Settings.Secure.getString(appC.getContentResolver(), Secure.ANDROID_ID))){
						Log.d("Bilhete", "Forgery");
						//TODO handle this
					}
				}
				obj.put("signature", c.getString(c.getColumnIndex(KEY_SIGNATURE)));
				return obj;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
		
	}
}