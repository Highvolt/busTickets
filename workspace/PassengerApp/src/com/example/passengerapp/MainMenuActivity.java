package com.example.passengerapp;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity implements RequestResultCallback {
	
	public static final String SERVER_ADDRESS = "http://cmov.inphormatic.us/";
	public static final int REQCODE_REGISTER = 101;
	public static final int REQCODE_LOGIN = 102;
	public static final int REQCODE_WALLET = 103;
	
	public static final String WALLET_URL = "/myTickets";
	
	private boolean hasAccount = false;
	private String authToken = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainmenu);
		
		SharedPreferences settings = getSharedPreferences("user_details", MODE_PRIVATE);
		hasAccount = settings.getBoolean("has_account", false);
		authToken = settings.getString("auth_token", "undefined");
		
		if(!hasAccount) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQCODE_LOGIN);
		}
		
		
		Button buyTickets = (Button) findViewById(R.id.Button01);
		Button ticketWallet = (Button) findViewById(R.id.Button02);
		Button activeTicket = (Button) findViewById(R.id.Button03);
		Button validateTicket = (Button) findViewById(R.id.Button04);
		Button inspectTicket = (Button) findViewById(R.id.Button05);
		
		buyTickets.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), BuyActivity.class);
				startActivity(i);
				
			}
		});
		
		ticketWallet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), WalletActivity.class);
				startActivity(i);
				
			}
		});
		
		activeTicket.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), CurrentTicketActivity.class);
				startActivity(i);
				
			}
		});
		
		validateTicket.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ValidateTicketActivity.class);
				startActivity(i);
				
			}
		});
		
		inspectTicket.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), InspectTicketActivity.class);
				startActivity(i);
				
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQCODE_LOGIN) {
                if(resultCode == Activity.RESULT_CANCELED) {
                        finish();
                } else {
                        SharedPreferences settings = getSharedPreferences("user_details", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        hasAccount = data.getBooleanExtra("hasAccountTemp", false);
                        authToken = data.getStringExtra("authTokenTemp");
                       
                        editor.putBoolean("hasAccount", hasAccount);
                        editor.putString("authToken", authToken);
                        editor.commit();
                }
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
			    startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
			    return true;
			case R.id.settings:
			    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			    return true;
			default:
			    return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onRequestResult(boolean result, JSONObject data, int requestCode) {
		// TODO Auto-generated method stub
		
	}

}
