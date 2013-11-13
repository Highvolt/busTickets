package pt.fe.up.cmov.busticket.inspector;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import pt.fe.up.cmov.busticket.inspector.R;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
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
	private long useTime;
	
	
	private void genQrCode(JSONObject qrCode){
			Log.d(this.getClass().getName(),qrCode.toString());
			/*String val=qrCode.getString("time")+','+qrCode.getString("validation")+','+qrCode.getString("busId")+','+qrCode.getString("device")+','+
					qrCode.getString("useDate")+','+qrCode.getString("type")+','+qrCode.getString("user");*/
			try {
				qrCode.put("time", new String(Base64.encode(ByteBuffer.allocate(8).putLong(Long.parseLong(qrCode.getString("time"))).array(), Base64.NO_WRAP)));
				qrCode.put("useDate", new String(Base64.encode(ByteBuffer.allocate(8).putLong(Long.parseLong(qrCode.getString("useDate"))).array(), Base64.NO_WRAP)));
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
			useTime=-1;
			try {
				useTime = Long.parseLong(lastValid.getString("useDate"));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d(this.getClass().getName(), lastValid.toString());
			lastValid.remove("signature");
			genQrCode(lastValid);
			try {
				((TextView)findViewById(R.id.validate_type)).setText(lastValid.getString("type"));
				/*ByteBuffer bf=ByteBuffer.allocate(8);
				bf.put(Base64.decode(lastValid.getString("useDate").getBytes(),Base64.DEFAULT));
				bf.flip();
				Long.toString(bf.getLong())*/
				updateGUI();
				
				((TextView)findViewById(R.id.validate_bus)).setText(lastValid.getString("type"));
				Timer myTimer = new Timer();
				
			      myTimer.schedule(new TimerTask() {
			         @Override
			         public void run() {updateGUI();}
			      }, 0, 1000);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			Log.d(this.getClass().getName(), "No Valid Ticket Found");
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Sem bilhete valido");
            alertDialog.setMessage("Não tem nenhum bilhete validado.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                    }
            });
            alertDialog.show();
			
		}
		
	}
	
	private void updateGUI(){
		long a=(System.currentTimeMillis()-useTime)/1000;
		
		final String timeToScreen=""+((a/60/60)>0?a/60/60+":":"")+((a/60)%60>=10?(a/60)%60:"0"+(a/60)%60)+":"+((a%60)>=10?(a%60):"0"+(a%60));
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				((TextView)findViewById(R.id.validate_time)).setText(timeToScreen);
			}
		});
		
		
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
