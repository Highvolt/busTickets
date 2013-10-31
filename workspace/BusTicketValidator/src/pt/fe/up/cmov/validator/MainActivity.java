package pt.fe.up.cmov.validator;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;

import android.os.Bundle;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private static final int EnableBluetooth=4;
	private static final String my_UUID="00001140-0000-1000-8000-00805F9B34FB";
	QRCodeEncoder qrCodeEncoder = null;
	BluetoothAdapter blueAdapter=null;
	ServerService serverService=null;
	private static String otp="0000";
	public static String bluetoothAccept="pt.fe.up.cmov.validator.ClientAccepted";
	private BroadcastReceiver bluetoothServer;
	public static AtomicBoolean serviceOn=new AtomicBoolean(false);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
	        Display display = manager.getDefaultDisplay();
	   int width = display.getWidth();
	   int height = display.getHeight();
	   int smallerDimension = width < height ? width : height;
	   smallerDimension = smallerDimension * 7 / 8;
	   JSONObject qrCode=new JSONObject();
	   try {
		qrCode.accumulate("mac", BluetoothAdapter.getDefaultAdapter().getAddress());
		//qrCode.accumulate("UUID", my_UUID);
		qrCode.accumulate("otp",otp);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		qrCodeEncoder = new QRCodeEncoder(qrCode.toString(), null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), smallerDimension);
		Bitmap bitmap=null;
		try {
			bitmap = qrCodeEncoder.encodeAsBitmap();
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bitmap==null){
			Log.d("MainActivity","QR is null");
		}
        ImageView view = (ImageView) findViewById(R.id.imageView1);
        view.setImageBitmap(bitmap);
        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				initBluetooth();
			}
		});
        this.bluetoothServer=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String tmp=intent.getStringExtra("msg");
				final String msg;
				if(tmp!=null){
					msg=tmp; 
				}else{
					msg="Connected peer!";
				}
				MainActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
						
					}
				});
				
			}
		};
		IntentFilter blue=new IntentFilter(MainActivity.bluetoothAccept);
		this.registerReceiver(this.bluetoothServer, blue);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(this.bluetoothServer);
	}
	private void initBluetooth() {
		blueAdapter = BluetoothAdapter.getDefaultAdapter();
		Log.d("Main Activity","Blue "+ blueAdapter);
    	if(blueAdapter != null)
    	{
    		// Bluetooth is enabled.
    		if (blueAdapter.isEnabled()) {
    			//action if enabled
    			try {
					startBluetoothService();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		else
    		{
    			Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    			startActivityForResult(discoverableIntent, EnableBluetooth);
    		}
    	}
    }
	
	private void startBluetoothService() throws IOException {
		// TODO Auto-generated method stub
		if(blueAdapter!=null){
			if(blueAdapter.isEnabled()){
				BluetoothServerSocket bs=blueAdapter.listenUsingInsecureRfcommWithServiceRecord("busTicketValidator",UUID.fromString(my_UUID));
				ServerService.socket=bs;
				Intent serverSocket=new Intent(this,ServerService.class);
				startService(serverSocket);
			}else{
				//exception needed
				return;
				
			}
		}else{
			//exception needed
			return;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case EnableBluetooth:
			if(resultCode==Activity.RESULT_OK){
				Log.d("Main Activity Request Code","successful bluetooth activation");
				//action enable
				try {
					startBluetoothService();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Log.d("Main Activity Request Code","user canceled bluetooth activation");
				//handle failure
			}
			
			break;

		default:
			break;
		}
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
