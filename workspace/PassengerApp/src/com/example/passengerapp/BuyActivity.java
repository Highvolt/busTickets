package com.example.passengerapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BuyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		
		HorizontalNumberPicker t1Picker = (HorizontalNumberPicker) findViewById(R.id.BuyPickerT1);
		HorizontalNumberPicker t2Picker = (HorizontalNumberPicker) findViewById(R.id.BuyPickerT2);
		HorizontalNumberPicker t3Picker = (HorizontalNumberPicker) findViewById(R.id.BuyPickerT3);
		
		
		
		Button t1Minus = (Button) t1Picker.findViewById(R.id.btn_minus);
		final EditText t1Value = (EditText) t1Picker.findViewById(R.id.edit_text_picker);
		t1Minus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int currentValue = Integer.parseInt(t1Value.getText().toString());
				if(currentValue > 0){
					t1Value.setText(String.valueOf(currentValue-1));
				}
			}
		});
		
		Button t1Plus = (Button) t1Picker.findViewById(R.id.btn_plus);
		t1Plus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int currentValue = Integer.parseInt(t1Value.getText().toString());
				if(currentValue < 10){
					t1Value.setText(String.valueOf(currentValue+1));
				}
			}
		});
		
		Button t2Minus = (Button) t2Picker.findViewById(R.id.btn_minus);
		final EditText t2Value = (EditText) t2Picker.findViewById(R.id.edit_text_picker);
		t2Minus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int currentValue = Integer.parseInt(t2Value.getText().toString());
				if(currentValue > 0){
					t2Value.setText(String.valueOf(currentValue-1));
				}
			}
		});
		
		Button t2Plus = (Button) t2Picker.findViewById(R.id.btn_plus);
		t2Plus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int currentValue = Integer.parseInt(t2Value.getText().toString());
				if(currentValue < 10){
					t2Value.setText(String.valueOf(currentValue+1));
				}
			}
		});
		
		
		Button t3Minus = (Button) t3Picker.findViewById(R.id.btn_minus);
		final EditText t3Value = (EditText) t3Picker.findViewById(R.id.edit_text_picker);
		t3Minus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int currentValue = Integer.parseInt(t3Value.getText().toString());
				if(currentValue > 0){
					t3Value.setText(String.valueOf(currentValue-1));
				}
			}
		});
		
		Button t3Plus = (Button) t3Picker.findViewById(R.id.btn_plus);
		t3Plus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int currentValue = Integer.parseInt(t3Value.getText().toString());
				if(currentValue < 10){
					t3Value.setText(String.valueOf(currentValue+1));
				}
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
