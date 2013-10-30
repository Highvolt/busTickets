package pt.fe.up.cmov.validator;

import java.io.IOException;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class ServerService extends IntentService {
	
	public static BluetoothServerSocket socket=null;
	public static Boolean running=true;
	public ServerService(){
		super("ServerBlue");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("Started", "Intent Started for Server");
		while(true){
			try {
				Log.d("Server Service", "accept Block");
				BluetoothSocket dev=socket.accept();
				this.sendBroadcast(new Intent(MainActivity.bluetoothAccept));
				Log.d("Server Service","Connected client");
				ClientHandler cConn=new ClientHandler(dev);
				cConn.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
