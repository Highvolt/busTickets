package pt.fe.up.cmov.busticket.inspector;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.method.MovementMethod;
import android.util.Base64;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{
	public static final Integer[] tempos=new Integer[]{15,30,60};
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "storedTickets";
	
	private static final String TABLE_TICKETS = "tickets";
	private static final String TABLE_VALIDATIONS = "validations";
	
	private static final String KEY_USERID = "user_id";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TIME = "time";
    private static final String KEY_SIGNATURE = "signature";
    private static final String KEY_USETIME = "useDate";
    
    private static final String KEY_VALIDATION_TIME = "validation_time";
    private static final String KEY_BUSID = "bus_id";
    private static final String KEY_BUS_SIGNATURE = "bus_signature";
	private static final String KEY_RESERVED="reserved";
	
	protected static String PUBKEY;
    
   
	public void reset(){
		this.getWritableDatabase().delete(TABLE_TICKETS, null, null);
	}
	
	public void reset2(Context mainMenuActivity){
		this.getWritableDatabase().delete(TABLE_TICKETS, null, null);
		SharedPreferences settings = mainMenuActivity.getSharedPreferences("user_details", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("hasAccount");
        editor.remove("authToken");
       
        
        editor.commit();
		
	}
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
				+KEY_RESERVED+" Text," 
				+KEY_BUSID+		" INTEGER,"
                + KEY_USETIME + " TEXT" +")";
		
		
        db.execSQL(CREATE_TICKETS_TABLE);
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALIDATIONS);
        // Create tables again
        onCreate(db);
	}
	
	public long addTicket(Ticket t){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_USERID, t.getUserID());
		values.put(KEY_DEVICE, t.getDeviceID());
		values.put(KEY_TYPE, t.getType());
		values.put(KEY_TIME, t.getTime());
		values.put(KEY_USETIME, t.getSignature());
		values.put(KEY_RESERVED, t.getReserved());
		values.put(KEY_BUSID, t.getBUS());
		long res=db.insert(TABLE_TICKETS, null, values);
		db.close();
		return res;
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
	
	public JSONObject getLastValidated(Context appC){
		JSONObject obj=new JSONObject();
		String query="SELECT * FROM "+ TABLE_TICKETS + " t,"+ TABLE_VALIDATIONS+" v WHERE v."+KEY_TIME+"=t.time order by "+KEY_VALIDATION_TIME+" desc";
		SQLiteDatabase db= this.getReadableDatabase();
		Cursor c=db.rawQuery(query, null);
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
				obj.put("useDate", c.getString(c.getColumnIndex(KEY_VALIDATION_TIME)));
				obj.put("busId", c.getString(c.getColumnIndex(KEY_BUSID)));
				obj.put("validation", c.getString(c.getColumnIndex(KEY_BUS_SIGNATURE)));
				return obj;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return null;
		
	}
	
	public static boolean checkWifiConnection(Context appC) {
        // Enable WIFI.
        /*WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifi != null && !wifi.isWifiEnabled()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Exit");
                alertDialog.setMessage("You need network connection to login. " +
                                "Please enable a data connection and try again.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent();
                        setResult(Activity.RESULT_CANCELED, intent);
                        finish();
                        }
                });
                alertDialog.show(); d 
        }*/
		boolean internet=false;
		 NetworkInfo info = (NetworkInfo) ((ConnectivityManager) appC
				 .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		 if (info == null || !info.isConnected()) {
		        internet = false;
		    }else if (info.isRoaming()) {
			        // here is the roaming option you can change it if you want to
			        // disable internet while roaming, just return false
			    	internet = false;
			    }else{
			    	internet =  true;
			 }
		    
		    
		    return internet;
	 }
	
	
	public void updateDataTicketsFromServer(Context appC){
		if(checkWifiConnection(appC)){
			SharedPreferences settings = appC.getSharedPreferences("user_details", Activity.MODE_PRIVATE);
			boolean hasAccount = settings.getBoolean("hasAccount", false);
			String authToken = settings.getString("authToken", "undefined");
			if(hasAccount){
				JSONObject obj=new JSONObject();
				try {
					obj.put("key", authToken);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				RestClient r=(new RestClient(RestClient.APIurl+"myTickets", obj)).connect();
				JSONArray data=null;
				try {
					data=r.getAsJSONArray();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(data==null){
					return;
				}
				long found=0;
				for (int i = 0; i < data.length(); i++) {
					try {
						Ticket ad=new Ticket(data.getJSONObject(i));
						if(addTicket(ad)<0){
							found++;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.d("UpdateData","Found "+found+" - received: "+data.length());
			}
		}
	}
	
	public void insertValidation(JSONObject obj){
		String query="Insert into "+TABLE_VALIDATIONS+"("+KEY_TIME+","+KEY_VALIDATION_TIME+","+KEY_BUSID+","+KEY_BUS_SIGNATURE+") values (?,?,?,?)";
		SQLiteDatabase db= this.getWritableDatabase();
		try {
			db.execSQL(query, (new String[]{obj.getString("time"),obj.getString("useTime"),obj.getString("BusId"),obj.getString("validation")}));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private boolean verifySignature(JSONObject obj){
		Signature sig;
		try {
			sig = Signature.getInstance("SHA1WithRSA");
			KeyFactory fact=KeyFactory.getInstance("RSA");
			 byte[] clear = Base64.decode(DatabaseHandler.PUBKEY, Base64.DEFAULT);
			 X509EncodedKeySpec keySpec = new X509EncodedKeySpec(clear);
			 
			sig.initVerify(fact.generatePublic(keySpec));
			String a=obj.getString("time")+"-"+obj.getString("device")+"-"+obj.getString("useDate")+"-"+obj.getString("type")+"-"+obj.getString("busId");
			sig.update(a.getBytes());
			return sig.verify(Base64.decode(obj.getString("validation"), Base64.DEFAULT));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	public boolean verifyLocalDB(JSONObject obj){
		SQLiteDatabase db=getReadableDatabase();
		try {
			Cursor c=db.query(TABLE_TICKETS, null, KEY_TIME+"=?", (new String[]{obj.getString("time")}), null, null, null);
			if(c.moveToFirst()){
				return true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	public JSONObject validateTicket(JSONObject obj,Context appC){
		//time(base64),validation(base64),busId(int),device(string),useDate(base64),type(int),user(int)
		try {
			ByteBuffer bf=ByteBuffer.allocate(8);
			bf.put(Base64.decode(obj.getString("time").getBytes(),Base64.DEFAULT));
			bf.flip();
			long time=bf.getLong();
			obj.put("time", time);
			ByteBuffer bf2=ByteBuffer.allocate(8);
			bf2.put(Base64.decode(obj.getString("useDate").getBytes(),Base64.DEFAULT));
			bf2.flip();
			long useDate=bf2.getLong();
			obj.put("useDate", useDate);
			JSONObject res=new JSONObject();
			res.accumulate("signature", verifySignature(obj));
			res.accumulate("localdb", verifyLocalDB(obj));
			SharedPreferences settings = appC.getSharedPreferences("user_details", Activity.MODE_PRIVATE);
    		boolean hasAccount = settings.getBoolean("hasAccount", false);
    		String authToken = settings.getString("authToken", "undefined");
			if(checkWifiConnection(appC) && hasAccount){
				JSONObject req=new 
						JSONObject();
				req.accumulate("key", authToken);
				req.accumulate("ticket", obj);
				RestClient rest=new RestClient(RestClient.APIurl+"verifyTicket", req).connect();
				if(rest.status==200){
					if(rest.getAsJSONObject().getInt("valid")==1){
						res.accumulate("remotedb", true);
					}else{
						res.accumulate("remotedb", false);
						res.accumulate("reason", rest.getAsJSONObject().getString("reason"));
					}
				}else{
					res.accumulate("remotedb", false);
				}
			}
			JSONObject res2=new JSONObject();
			res2.accumulate("result", res);
			res2.accumulate("ticket", obj);
			return res2;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}