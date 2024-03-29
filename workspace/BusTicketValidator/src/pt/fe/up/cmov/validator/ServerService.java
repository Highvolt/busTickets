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
		Intent intent2=new Intent(MainActivity.bluetoothAccept);
		intent2.putExtra("status", 1);
		this.sendBroadcast(intent2);
		while(true){
			try {
				Log.d("Server Service", "accept Block");
				BluetoothSocket dev=socket.accept();
				this.sendBroadcast(new Intent(MainActivity.bluetoothAccept));
				Log.d("Server Service","Connected client");
				ClientHandler cConn=new ClientHandler(dev,this);
				cConn.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		intent2=new Intent(MainActivity.bluetoothAccept);
		intent2.putExtra("status", 0);
		this.sendBroadcast(intent2);

	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		MainActivity.serviceOn.set(true);
		Intent intent=new Intent(MainActivity.bluetoothAccept);
		intent.putExtra("msg", "Service Starting");
		this.sendBroadcast(intent);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MainActivity.serviceOn.set(false);
		Intent intent=new Intent(MainActivity.bluetoothAccept);
		intent.putExtra("msg", "Service Stopping");
		this.sendBroadcast(intent);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
