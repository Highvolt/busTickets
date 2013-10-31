package pt.fe.up.cmov.busticket.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;

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
	private static final UUID MY_UUID = UUID.fromString("00001140-0000-1000-8000-00805F9B34FB");
	private ArrayList<BluetoothDevice> devs=null;
	public static String QRresult="pt.fe.up.cmov.QRCODE";
	private JSONObject lastValue=null;
	private BluetoothSocket devS;
	private AtomicBoolean connected=new AtomicBoolean(false);
	private static final int retries=45
			;
	
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
	    	try {
				lastValue=new JSONObject(qr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				lastValue=null;
			}
	    
	    	
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
    			connectToValidator();
    		}
    		else
    		{
    			Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivityForResult(discoverableIntent, 4);
    		}
    	}
    }
	
	private void connectToValidator() {
		if(lastValue!=null){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					ObjectOutputStream bo=null;
					ObjectInputStream bi=null;
					BluetoothDevice dev;
					try {
						dev = blueAdapter.getRemoteDevice(lastValue.getString("mac"));
						devS=dev.createInsecureRfcommSocketToServiceRecord(MY_UUID);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					connected.set(false);
					int tries=0;
					while(tries<MainActivity.retries && !connected.get()){
						tries++;
						Log.d("Client Connecting","Try #"+tries);
						Thread t=new Thread(new Runnable() {
							
							@Override
							public void run() {
							 try{	
								 		blueAdapter.cancelDiscovery();
										Log.d("Bluetooth", "Trying!!!");
										devS.connect();
										Log.d("Bluetooth", "Connected fixe");
										connected.set(true);
										Log.d("Bluetooth", "Connected flag "+connected.get());
										
			                         }catch(Exception e){
			                        	e.printStackTrace();
			                        	try {
											Thread.sleep(1000);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
			                        	
			                         }finally{
			                        	 Thread.currentThread().getThreadGroup().getParent().interrupt();
			                        	 
			                         }
									
								
								
							}
						});
						t.start();
						try {
							Thread.sleep(7000);
							Log.d("Bluetooth timeout", "Boolean value: "+connected.get());
							if(connected.get()==false){
								t.interrupt();
								
								Log.d("Bluetooth Timeout", "Time out validator connection");
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
						
						
					}
					
					try {
						
						Log.d("Bluetooth connect","Connection with device "+devS.getRemoteDevice().getAddress()+" successful");
						Thread.sleep(150);
						bo=new ObjectOutputStream(devS.getOutputStream());
						bi=new ObjectInputStream(devS.getInputStream());
						JSONObject jobj=new JSONObject();
						jobj.accumulate("text", MessageDigest.getInstance("MD5").digest(Long.toString(System.currentTimeMillis()).getBytes()));
						bo.writeObject(jobj.toString());
						bo.flush();
						Log.d("Main Activity", (String) bi.readObject());
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("Bluetooth connect", "Connection Failed");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						
							try {
								if(bo!=null)
									bo.close();
								if(bi!=null)
									bi.close();
								if(devS!=null)
									devS.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
					}
					
				}
			}).start();
			
		}
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Log.d("MainActivity","Return "+requestCode);
    	switch (requestCode) {
		case 4:
			//startDiscovery();
			connectToValidator();
			break;
		case 2:
			Log.d("MainActivity", "Returned "+data.getAction()+" == "+MainActivity.QRresult);
			if(data.getAction().equals(MainActivity.QRresult)){
				Log.d("MainActivity", "Process QR");
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