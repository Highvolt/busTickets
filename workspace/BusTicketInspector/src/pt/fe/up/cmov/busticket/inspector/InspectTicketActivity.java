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
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InspectTicketActivity extends Activity {
	DatabaseHandler db=null;
	TextView type=null;
	TextView bus=null;
	TextView time=null;
	TextView local=null;
	TextView remote=null;
	TextView signature=null;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspect_ticket);
		Intent i=getIntent();
		String ticket=i.getStringExtra("ticket");
		String result=i.getStringExtra("result");
		if(ticket==null || result==null){
			Toast.makeText(this, "Invalido", Toast.LENGTH_SHORT);
			return;
		}
		try {
			JSONObject t=new JSONObject(ticket);
			JSONObject r=new JSONObject(result);
			type=(TextView) findViewById(R.id.validate_type);
			type.setText(t.getString("type"));
			bus=(TextView) findViewById(R.id.validate_bus);
			bus.setText(t.getString("busId"));
			time=(TextView) findViewById(R.id.validate_time);
			long dur=(DatabaseHandler.tempos[t.getInt("type")-1]-((System.currentTimeMillis()-t.getLong("useDate"))/1000/60));
			time.setText(""+dur);
			local=(TextView) findViewById(R.id.validate_local);
			local.setText(r.getBoolean("localdb")?"ok":"falhou");
			remote=(TextView) findViewById(R.id.validate_remote);
			boolean remoteH=false;
			boolean remoteDB=false;
			if(r.has("remotedb")){
				remoteH=true;
				remoteDB=r.getBoolean("remotedb");
				if(r.has("reason")){
					remote.setText(r.getBoolean("remotedb")?"ok":r.getString("reason"));
				}else{
					
					remote.setText(r.getBoolean("remotedb")?"ok":"falhou");
				}
			}else{
				remote.setText("N/A");
				remote.setTextColor(Color.YELLOW);
			}
			signature=(TextView) findViewById(R.id.validate_signature);
			signature.setText(r.getBoolean("signature")?"ok":"falhou");
			boolean sig=r.getBoolean("signature");
			boolean localDB=r.getBoolean("localdb");
			
			
			if(sig){
				signature.setTextColor(Color.GREEN);
			}else{
				signature.setTextColor(Color.RED);
			}
			if(localDB){
				local.setTextColor(Color.GREEN);
			}else{
				local.setTextColor(Color.RED);
			}
			if(remoteH){
				if(remoteDB){
					remote.setTextColor(Color.GREEN);
				}else{
					remote.setTextColor(Color.RED);
				}
			}
			
			if(((sig && localDB && remoteDB && remoteH) || (sig && remoteDB && remoteH)) && dur>=0){
				
				((ImageView) findViewById(R.id.validate_img)).setImageResource(R.drawable.approve3);
			}else if((sig && localDB && !remoteH && dur>=0)){
				((ImageView) findViewById(R.id.validate_img)).setImageResource(R.drawable.work);
			}else{
				((ImageView) findViewById(R.id.validate_img)).setImageResource(R.drawable.close15);
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "Invalido", Toast.LENGTH_SHORT);
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
