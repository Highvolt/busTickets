package pt.fe.up.cmov.validator;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ValidatorDatabaseHelper extends SQLiteOpenHelper {
	
	public ValidatorDatabaseHelper(Context context) {
		super(context, "vdb.db", null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	private static final int DATABASE_VERSION = 3;
   
    private static final String TICKET_TABLE_CREATE =
    		"Create Table Ticket (" +
					"type integer not null," +
					"userid integer not null," +
					"devid varchar not null," +
					"usedate datetime not null," +
					"reserved varchar," +
					"time datetime,"+
					"Primary key (userid,usedate)" +
					");";
    private static final String KEYS_TABLE_CREATE =
    		"Create Table Keys (" +
					"creationDate date not null Primary Key," +
					"privKey varchar not null," +
					"pubKey varchar not null" +
					");";
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
		arg0.execSQL(TICKET_TABLE_CREATE);
		
		arg0.execSQL(KEYS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("Drop table if exists ticket;");
		db.execSQL("Drop table if exists keys;");
		onCreate(db);
	}
	
	public void markTicketAsDuplicate(JSONObject obj){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cs=new ContentValues();
		try {

			cs.put("reserved",obj.getString("useDateFromServer"));
			db.update("Ticket", cs, "userid=? and usedate=?", new String[]{obj.getString("user"),obj.getString("useDate")});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void insertTicket(JSONObject obj){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cs=new ContentValues();
		try {
			cs.put("type",obj.getString("type"));
			cs.put("userid", obj.getString("user"));
			cs.put("devid", obj.getString("device"));
			cs.put("usedate", obj.getString("useDate"));
			cs.put("time", obj.getString("time"));
			db.insert("Ticket",null,cs);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public ArrayList<JSONObject> getAllTickets() {
		// TODO Auto-generated method stub\
		SQLiteDatabase db=this.getReadableDatabase();
		ArrayList<JSONObject> a=new ArrayList<JSONObject>();
		Cursor c=db.query("Ticket", null, null, null, null, null, null);
		if(c.moveToFirst()){
		while (c.moveToNext() ) {
			
				JSONObject o=new JSONObject();
				try {
					o.accumulate("type", c.getString(c.getColumnIndex("type")));
					o.accumulate("user", c.getString(c.getColumnIndex("userid")));
					o.accumulate("device", c.getString(c.getColumnIndex("devid")));
					o.accumulate("useDate", c.getString(c.getColumnIndex("usedate")));
					o.accumulate("reserved", c.getString(c.getColumnIndex("reserved")));
					o.accumulate("time", c.getString(c.getColumnIndex("time")));
					o.accumulate("busId",ValidatorData.INSTANCE.id);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				a.add(o);
			}
			
		
		}
		return a;
	}

}
