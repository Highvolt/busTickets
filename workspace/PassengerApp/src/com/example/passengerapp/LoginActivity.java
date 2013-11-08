package com.example.passengerapp;

import org.json.JSONObject;

import com.example.passengerapp.APIRequestTask.HttpRequestType;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements RequestResultCallback {
	
	public static final int REQCODE_LOGIN = 102;
    public String LOGIN_URL = "http://localhost/";

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
				startActivity(i);
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
            
            // Create and call HTTP request.
            APIRequestTask task = new APIRequestTask(this, HttpRequestType.Post, json,
                            LOGIN_URL, "Logging in.", REQCODE_LOGIN);
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
	public void onRequestResult(boolean result, JSONObject data, int requestCode) {
		// TODO Auto-generated method stub
		
	}

}
