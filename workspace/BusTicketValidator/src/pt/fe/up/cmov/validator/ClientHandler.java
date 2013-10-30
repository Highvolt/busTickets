package pt.fe.up.cmov.validator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ClientHandler extends Thread {
	private BluetoothSocket client;
	
	public ClientHandler(BluetoothSocket client) {
		this.client=client;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		 InputStreamReader bi=null;
		try {
			bi=new InputStreamReader(client.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buffer=new byte[255];
		int validBytes=0;
		try {
			
			Log.d("Client Hanfler", Integer.toHexString(bi.read()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bi.close();
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
}
