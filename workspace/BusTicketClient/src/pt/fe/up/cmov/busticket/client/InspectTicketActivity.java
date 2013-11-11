package pt.fe.up.cmov.busticket.client;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;


import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class InspectTicketActivity extends Activity {
	DatabaseHandler db=null;
	private ImageView image;
	private int smallerDimension;
	
	
	private void genQrCode(JSONObject qrCode){
			Log.d(this.getClass().getName(),qrCode.toString());
			/*String val=qrCode.getString("time")+','+qrCode.getString("validation")+','+qrCode.getString("busId")+','+qrCode.getString("device")+','+
					qrCode.getString("useDate")+','+qrCode.getString("type")+','+qrCode.getString("user");*/
			try {
				qrCode.put("time", Base64.encode(ByteBuffer.allocate(8).putLong(Long.parseLong(qrCode.getString("time"))).array(), Base64.DEFAULT));
				qrCode.put("useDate", Base64.encode(ByteBuffer.allocate(8).putLong(Long.parseLong(qrCode.getString("useDate"))).array(), Base64.DEFAULT));
				QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrCode.toString(), null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), smallerDimension);
				
				try {
					final Bitmap bitmap= qrCodeEncoder.encodeAsBitmap();
					if(bitmap==null){
						Log.d("MainActivity","QR is null");
					}
				    runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							image.setImageBitmap(bitmap);
							
						}
					});
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
	        
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspect_ticket);
		 WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
	        Display display = manager.getDefaultDisplay();
	        image = (ImageView) findViewById(R.id.validate_img);     
	   int width = display.getWidth();
	   int height = display.getHeight();
	   smallerDimension = width < height ? width : height;
	   
		db=new DatabaseHandler(getApplicationContext());
		JSONObject lastValid=db.getLastValidated(getApplicationContext());
		if(lastValid!=null){
			Log.d(this.getClass().getName(), lastValid.toString());
			lastValid.remove("signature");
			genQrCode(lastValid);
			try {
				((TextView)findViewById(R.id.validate_type)).setText(lastValid.getString("type"));
				((TextView)findViewById(R.id.validate_time)).setText(lastValid.getString("useDate"));
				((TextView)findViewById(R.id.validate_bus)).setText(lastValid.getString("type"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			Log.d(this.getClass().getName(), "No Valid Ticket Found");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.home:
			    startActivity(new Intent(this, MainMenuActivity.class));
			    return true;
			case R.id.settings:
			    startActivity(new Intent(this, SettingsActivity.class));
			    return true;
			default:
			    return super.onOptionsItemSelected(item);
		}
	}

}
