package pt.fe.up.cmov;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public enum Misc {
	INSTANCE;
	private String devID;
	private Misc(){
		
	}
	
	public String getDevId(){
		 return devID;
	}
	
	public void setDevID(String id){
		devID=id;
	}
	
	public static boolean checkWifiConnection(Context appC) {

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
}
