package pt.fe.up.cmov.busticket.client;

import org.json.JSONException;
import org.json.JSONObject;

import pt.fe.up.cmov.busticket.client.APIRequestTask.HttpRequestType;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements RequestResultCallback {
	
	public static final int REQCODE_LOGIN = 102;
	public static final int REQCODE_REGISTER = 101;
    public String LOGIN_URL = "/login";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		TextView registerLink = (TextView) findViewById(R.id.link_to_register);
		registerLink.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivityForResult(i, REQCODE_REGISTER);
			}
			
		});
		
		Button loginButton = (Button) findViewById(R.id.btnLogin);
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText login = (EditText) findViewById(R.id.login_user);
				EditText password = (EditText) findViewById(R.id.login_password);
				
				String loginField = login.getText().toString().trim();
				String passwordField = password.getText().toString().trim();
				
				if(loginField.length() < 5){
					Toast.makeText(getApplicationContext(), "Invalid login.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(passwordField.length() < 5){
					Toast.makeText(getApplicationContext(), "Invalid password.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				buildLoginRequest(loginField, passwordField);
			}
			
		});
		
		checkWifiConnection();
	}
	
	protected void buildLoginRequest(String loginField, String passwordField) {
		try {
            // Create JSON request data.
            JSONObject json = new JSONObject();
            json.put("username", loginField);
            json.put("password", passwordField);
            json.put("device", Settings.Secure.getString(getContentResolver(), Secure.ANDROID_ID));
            
            // Create and call HTTP request.
            APIRequestTask task = new APIRequestTask(this, HttpRequestType.Post, json,
                            MainMenuActivity.SERVER_ADDRESS + LOGIN_URL, "Logging in.", REQCODE_LOGIN);
            task.execute((Void[]) null);
		} catch (Exception e) {}
		
	}

	private void checkWifiConnection() {
        // Enable WIFI.
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifi != null && !wifi.isWifiEnabled()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Exit");
                alertDialog.setMessage("You need network connection to login. " +
                                "Please enable a data connection and try again.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new AlertDialog.OnClickListener() {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	@Override
    public void onBackPressed() {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Exit");
            alertDialog.setMessage("This app needs an account to be used. Are you sure you want to leave?");
            
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {}
            });

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            
                    intent.putExtra("hasAccountTemp", false);
                    intent.putExtra("authTokenTemp", "");
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                    }
            });
            alertDialog.show();
    }

	@Override
	public void onRequestResult(int responseCode, boolean result, JSONObject data, int requestCode) {
		if(result){
			try {
				if(data.has("msg")){
					String message = data.getString("msg");
                	if(message.equals("Bad Auth information"))
                		Toast.makeText(getApplicationContext(),"Invalid login.", Toast.LENGTH_SHORT).show();
                	else
                		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
				}
				else if(data.has("token")){
					Toast.makeText(getApplicationContext(),"Login successful!", Toast.LENGTH_SHORT).show();
					Intent i = new Intent();
					i.putExtra("hasAccountTemp", true);
               	    i.putExtra("authTokenTemp", data.getString("token"));
               	    setResult(Activity.RESULT_OK, i);
					finish();
				}
				
			} catch (JSONException e) {
				Log.e("Req_tag", "Error getting result.", e);
                Toast.makeText(getApplicationContext(),
                                "A problem was encountered. Pleasy try again later.", 
                                Toast.LENGTH_SHORT).show();
			}			
		}else{
			Log.e("Req_tag", "Request failed.");
            Toast.makeText(getApplicationContext(),"A problem was encountered. Pleasy try again later.", 
                            Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Intent i = new Intent();
        if(resultCode == Activity.RESULT_CANCELED) {
        	setResult(Activity.RESULT_CANCELED, i);
            finish();
        } else {
            String authToken = data.getStringExtra("authTokenTemp");
            boolean hasAccount = data.getBooleanExtra("hasAccountTemp", false);
            i.putExtra("hasAccountTemp", hasAccount);
       	    i.putExtra("authTokenTemp", authToken);
       	    setResult(Activity.RESULT_OK, i);
       	    finish();
        }
	}
	
	

}
