package com.example.passengerapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainmenu);
		
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

}
