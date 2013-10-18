package pt.fe.up.cmov.busticket.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

	BluetoothAdapter blueAdapter;

	
	private static final int REQUEST_DISCOVERABLE_RESULT = 100001;
	private static final UUID MY_UUID = UUID.fromString("00000003-3713-11e3-aa6e-0800200c9a66");
	private ArrayList<BluetoothDevice> devs=null;
	public static String QRresult="pt.fe.up.cmov.QRCODE";
	private static IntentFilter filterQR=null;
	
	
	private void qrProcess(Intent intent) {
	    	String action = intent.getAction();
	    	Log.d("MainActivity",intent.getStringExtra("pt.cmov.qrCode"));
	    	final String qr=intent.getStringExtra("pt.cmov.qrCode");
	    	MainActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Log.d("MainActivity",qr+" to GUI.");
					TextView t=(TextView) MainActivity.this.findViewById(R.id.textView2);
					t.setText(qr);
					
				}
			});
	    	
	}
	
	
	private final BroadcastReceiver mReceiverUUIDS = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        BluetoothDevice deviceExtra = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
	        Parcelable[] uuidExtra = intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
	        Method uuidsMeth=null;
           ParcelUuid[] uuids=null;
	        //Parse the UUIDs and get the one you are interested in
           if(deviceExtra==null){
        	   Log.d("MainActivity","Error device");
        	   return;
           }
	        Log.d("MainActivity", "UIID device: " + deviceExtra.getName());
            if(uuidExtra!=null){
            	Log.d("MainActivity", "Found UUID: " + uuidExtra.length + " UUID.");
	        }else{
	        	try {
					uuidsMeth = deviceExtra.getClass().getMethod("getUuids", null);
					 uuids= (ParcelUuid[]) uuidsMeth.invoke(deviceExtra);
					if(uuids==null){
						Log.d("MainActivity", "getUuids error!");
						return;
					}
					Log.d("MainActivity", "Found UUID: " + uuids.length + " UUID.");
		            for(ParcelUuid uuid : uuids) {
		            	Log.d("MainActivity", "UUID: " + uuid.toString());
		            	if(MY_UUID.equals(uuid.getUuid())) {
			            	BluetoothSocket tmp = null;
			                try {
			                	blueAdapter.cancelDiscovery();
			                	Log.d("MainActivity", "UUID: " + uuid.getUuid().toString());
			                    tmp = deviceExtra.createInsecureRfcommSocketToServiceRecord(uuid.getUuid());
			                    tmp.connect();
			                    tmp.close();
			                    break;
			                } catch (IOException e) { 
			                	Log.e("MainActivity", e.getMessage());
			                	Log.e("MainActivity", "Failed to connect.");
			                }
			            }	
		            }
	        	} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	    }
	};
	

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
	        	Log.d("MainActivity", "Start Discovery");
	        }else
	        	if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
	 	        	Log.d("MainActivity", "Finished Discovery");
	 	        }else
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            
	            Log.d("MainActivity", "Found "+device.getName()+".");
	            devs.add(device);
	            
				
				/*
	            try {
					BluetoothSocket bs=device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
					blueAdapter.cancelDiscovery();
					bs.connect();
					if(bs!=null){
						 Log.d("MainActivity", "Service "+device.getName()+" ok.");
						 
					}
	            } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            */
	           
	          
	        }
	    }

		
		
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				devs= new ArrayList<BluetoothDevice>();
				initBluetooth();
			}
		});
		Button button1= (Button) findViewById(R.id.button2);
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent a=new Intent(MainActivity.this, com.jwetherell.quick_response_code.CaptureActivity.class);
				startActivityForResult(a, 2);
			}
		});
		
		
	}
	
	private void initBluetooth() {
		blueAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(blueAdapter != null)
    	{
    		// Bluetooth is enabled.
    		if (blueAdapter.isEnabled()) {
    			startDiscovery();
    		}
    		else
    		{
    			Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_RESULT);
    		}
    	}
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Log.d("MainActivity","Return "+requestCode);
    	switch (requestCode) {
		case REQUEST_DISCOVERABLE_RESULT:
			startDiscovery();
			break;
		case 2:
			if(data.getAction()==QRresult){
				qrProcess(data);
			}
			
		default:
			break;
		}
    }
	
	public void startDiscovery() {
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		blueAdapter.startDiscovery();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
