package pt.fe.up.cmov.validator;

import org.json.JSONException;
import org.json.JSONObject;

import pt.fe.up.cmov.Misc;
import pt.fe.up.cmov.RestClient;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BusValidator extends Activity {
	TextView idAutocarro=null;
	EditText password=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.setAppContext(getApplicationContext());
		RestClient.appContext=getApplicationContext();
		ValidatorData db=ValidatorData.INSTANCE;
		setContentView(R.layout.activity_bus_validator);
		Misc.INSTANCE.setDevID(Secure.getString(this.getApplicationContext().getContentResolver(),  Secure.ANDROID_ID));
		
		idAutocarro=(TextView)findViewById(R.id.idAutocarro);
		idAutocarro.setText(Misc.INSTANCE.getDevId());
		password=(EditText) findViewById(R.id.password);
		Button login=(Button) findViewById(R.id.login);
		
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				(new Thread(new Runnable() {
					
					@Override
					public void run() {
						JSONObject obj=new JSONObject();
						try {
							obj.put("device", idAutocarro.getText().toString());
							obj.put("password", password.getText().toString());
							RestClient request=(new RestClient(RestClient.APIurl+"loginBust", obj)).connect();
							if(request.status==200){
								JSONObject resposta=request.getAsJSONObject();
								Log.d(getClass().getName(), resposta.toString());
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				})).start();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bus_validator, menu);
		return true;
	}
	
}
