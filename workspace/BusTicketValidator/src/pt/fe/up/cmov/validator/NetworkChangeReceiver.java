package pt.fe.up.cmov.validator;


import org.json.JSONException;
import org.json.JSONObject;

import pt.fe.up.cmov.RestClient;

import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.widget.Toast;
 
public class NetworkChangeReceiver extends BroadcastReceiver {
 

	public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Boolean running=Boolean.FALSE;
     
     
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
             
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        } 
        return TYPE_NOT_CONNECTED;
    }
     
    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn ==TYPE_WIFI) {
            status = "Wifi";
        } else if (conn ==TYPE_MOBILE) {
            status = "Mobile Data";
        } else if (conn ==TYPE_NOT_CONNECTED) {
            status = "Sem ligação";
        }
        return status;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
    	int value=getConnectivityStatus(context);
        if(value==TYPE_MOBILE || value==TYPE_WIFI){
        	if(running.booleanValue()){
        		return;
        	}
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					running=Boolean.TRUE;
					if(ValidatorData.INSTANCE.waitting.size()>0){
						while (!ValidatorData.INSTANCE.waitting.isEmpty()) {
							JSONObject obj=ValidatorData.INSTANCE.waitting.poll();
							RestClient r=new RestClient(RestClient.APIurl+"validateTicket", obj).connect();
							if(r.status==200){
								continue;
							}
							if(r.status==400){
								try {
									JSONObject a=r.getAsJSONObject();
									if(a.getString("reason").equals("fake")){
										//TODO handle fake
									}else{
										String b=""+a.getJSONObject("data").getString("useDate")+"|"+a.getJSONObject("data").getString("useBus");
										obj.put("useDateFromServer", b);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}else if(r.status==500 || r.status<=0){
								ValidatorData.INSTANCE.waitting.add(obj);
								return;
							}
						}
					}
					running=Boolean.FALSE;
					
				}
			}).start();
        }
        /*String status = getConnectivityStatusString(context);
        
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();*/
    }
    
    
}