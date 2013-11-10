package pt.fe.up.cmov.busticket.client;

import org.json.JSONException;
import org.json.JSONObject;

public class Ticket {
	int _userid;
	String _deviceID;
	int _type;
	long _time;
	String _signature;
	
	public Ticket(){
		
	}
	
	 public Ticket(int userID, String device, int type, long time, String signature){
	        this._userid = userID;
	        this._deviceID = device;
	        this._type = type;
	        this._time = time;
	        this._signature = signature;
	    }
	 
	 public Ticket(JSONObject json) {
		try {
			this._userid = json.getInt("user");
			this._deviceID = json.getString("device");
			this._signature = json.getString("signature");
			this._type = json.getInt("type");
			this._time = json.getLong("time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void setUserID(int id){
		 this._userid = id;
	 }
	 
	 public int getUserID(){
		 return this._userid;
	 }
	 
	 public void setDeviceID(String device){
		 this._deviceID = device;
	 }
	 
	 public String getDeviceID(){
		 return this._deviceID;
	 }
	 
	 public void setType(int type){
		 this._type = type;
	 }
	 
	 public int getType(){
		 return this._type;
	 }
	 
	 public void setTime(long time){
		 this._time = time;
	 }
	 
	 public long getTime(){
		 return this._time;
	 }
	 
	 public void setSignature(String sign){
		 this._signature = sign;
	 }
	 
	 public String getSignature(){
		 return this._signature;
	 }
}
