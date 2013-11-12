package pt.fe.up.cmov.busticket.client;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.widget.Toast;
 
public class NetworkChangeReceiver extends BroadcastReceiver {
 
	public static String action="pt.cmov.andaline.net";
	public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
     
     
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
 
        Intent int2=new Intent(action);
        int2.putExtra("net",getConnectivityStatus(context) );
        context.sendBroadcast(int2);
        /*String status = getConnectivityStatusString(context);
        
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();*/
    }
    
    
}