package pt.fe.up.cmov.validator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.CharBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

import org.json.JSONException;
import org.json.JSONObject;

import pt.fe.up.cmov.RestClient;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class ClientHandler extends Thread {
	private BluetoothSocket client;
	private ServerService sv;
	
	public ClientHandler(BluetoothSocket client, ServerService sv) {
		this.client=client;
		this.sv=sv;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		/*try {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			client.connect();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		*/
		 ObjectInputStream bi=null;
		 ObjectOutputStream bo=null;
		try {
			bi=new ObjectInputStream(client.getInputStream());
			bo=new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
		try {
			String received=(String) bi.readObject();
			JSONObject ticket=new JSONObject(received);
			ticket.put("useTime", Long.toString(System.currentTimeMillis()));
			Log.d("Client Handler","Readed " + received);
			/*JSONObject jobj=new JSONObject();
			jobj.accumulate("text","coisas lindas");*/
			ticket.accumulate("validation", ValidatorData.INSTANCE.signTicket(ticket));
			bo.writeObject(ticket.toString());
			bo.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			Intent intent=new Intent(MainActivity.bluetoothAccept);
			intent.putExtra("msg", "Peer <"+client.getRemoteDevice().getName()+"> disconnected");
			sv.sendBroadcast(intent);
		}
		
		
		
		
		
	}
}
