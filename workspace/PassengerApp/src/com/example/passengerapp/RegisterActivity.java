package com.example.passengerapp;

import java.lang.reflect.Field;
import java.util.Calendar;

import org.json.JSONObject;

import com.example.passengerapp.APIRequestTask;
import com.example.passengerapp.APIRequestTask.HttpRequestType;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements RequestResultCallback {
	
	private int pickerYear = 0;
    private int pickerMonth = 0;
    private int pickerDay = 0;
    public static final int REQCODE_REGISTER = 101;
    public String REGISTER_URL = "/register";
    
    DatePickerDialog.OnDateSetListener pickerDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                pickerYear = year;
                pickerMonth = monthOfYear;
                pickerDay = dayOfMonth;
                
                int localMonth = (pickerMonth + 1);
                String monthString = localMonth < 10 ? "0" + localMonth : Integer.toString(localMonth);
                String localYear = Integer.toString(pickerYear).substring(2);
                ((EditText) findViewById(R.id.reg_ccdate)).setText(new StringBuilder().append(monthString).append("/").append(localYear).append(" "));
        }

    };

	private DatePickerDialog createCustomDatePicker() {
	    DatePickerDialog dpd = new DatePickerDialog(this, pickerDateSetListener, pickerYear, pickerMonth, pickerDay);
	    try {
	            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
	            for (Field datePickerDialogField : datePickerDialogFields) {
	                    if (datePickerDialogField.getName().equals("mDatePicker")) {
	                            datePickerDialogField.setAccessible(true);
	                            DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
	                            Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
	                            for (Field datePickerField : datePickerFields) {
	                                    if ("mDayPicker".equals(datePickerField.getName()) || "mDaySpinner".equals(datePickerField.getName())) {
	                                            datePickerField.setAccessible(true);
	                                            Object dayPicker = new Object();
	                                            dayPicker = datePickerField.get(datePicker);
	                                            ((View) dayPicker).setVisibility(View.GONE);
	                                    }
	                            }
	                    }
	
	            }
	    } catch (Exception ex) {}
	    return dpd;
	}
	
	 private void checkWifiConnection() {
         // Enable WIFI.
         WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
         if(wifi != null && !wifi.isWifiEnabled()) {
                 AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                 alertDialog.setTitle("Exit");
                 alertDialog.setMessage("You need network connection to create an account. " +
                                 "Please enable a data connection and try again.");

                 // Ok button.
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Calendar c = Calendar.getInstance();
        pickerYear = c.get(Calendar.YEAR);
        pickerMonth = c.get(Calendar.MONTH) + 1;
		
		EditText datePicker = (EditText) findViewById(R.id.reg_ccdate);
		datePicker.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				createCustomDatePicker().show();
			}
			
		});
		
		Button submitButton = (Button) findViewById(R.id.btnRegister);
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText name = (EditText) findViewById(R.id.reg_username);
				EditText password = (EditText) findViewById(R.id.reg_password);
				EditText ccn = (EditText) findViewById(R.id.reg_ccn);
				EditText ccdate = (EditText) findViewById(R.id.reg_ccdate);
				EditText cc_csc = (EditText) findViewById(R.id.reg_cc_cvc);
				
				long dateUnixConversion = 0;
				
				String nameField = name.getText().toString().trim();
				if(nameField.length() < 5){
					Toast.makeText(getApplicationContext(), "Username is too short.", Toast.LENGTH_SHORT).show();
					return;
				}else if(nameField.length() > 25){
					Toast.makeText(getApplicationContext(), "Username is too long.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String passField = password.getText().toString().trim();
				if(passField.length() < 5){
					Toast.makeText(getApplicationContext(), "Password is too short.", Toast.LENGTH_SHORT).show();
					return;
				}else if(passField.length() > 30){
					Toast.makeText(getApplicationContext(), "Password is too long.", Toast.LENGTH_SHORT).show();
					return;
				}else if(passField.contains(" ")){
					Toast.makeText(getApplicationContext(), "Password can't contain spaces.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String ccnField = ccn.getText().toString().trim();
				if(ccnField.length() != 16){
					Toast.makeText(getApplicationContext(), "Credit card nº must be 16 digits long.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String cccodeField = cc_csc.getText().toString().trim();
				if(cccodeField.length() != 3){
					Toast.makeText(getApplicationContext(), "Security code must be 3 digits long.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String ccdateField = ccdate.getText().toString().trim();
				Calendar c = Calendar.getInstance();
				
				if(ccdateField.isEmpty()){
					Toast.makeText(getApplicationContext(), "Please enter card expiration date.", Toast.LENGTH_SHORT).show();
					return;
				}else if(pickerYear < c.get(Calendar.YEAR)){
					Toast.makeText(getApplicationContext(), "Current card has already expired.", Toast.LENGTH_SHORT).show();
					return;
				}else if(pickerYear == c.get(Calendar.YEAR) && pickerMonth < c.get(Calendar.MONTH)){
					Toast.makeText(getApplicationContext(), "Current card has already expired.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				
				c.set(pickerYear, pickerMonth, pickerDay);
				dateUnixConversion = (long) (c.getTimeInMillis() / 1000.0);
				
				buildRegisterRequest(nameField, passField, ccnField, dateUnixConversion, cccodeField);
			}
		});
		
		checkWifiConnection();
		
	}
	
	private void buildRegisterRequest(String name, String password, String ccNumber, long ccDate, String ccSecurityCode){
		try{
			JSONObject json = new JSONObject();
			json.put("username", name);
			json.put("password", password);
			json.put("device", Settings.Secure.getString(getContentResolver(), Secure.ANDROID_ID));
			json.put("number", ccNumber);
			json.put("expire", ccDate);
			json.put("csc", ccSecurityCode);
			
			//FAZER O REQUEST
			APIRequestTask request = new APIRequestTask(this, HttpRequestType.Post, json, MainMenuActivity.SERVER_ADDRESS + REGISTER_URL, "Creating account...", REQCODE_REGISTER);
			request.execute((Void[]) null);
			
		}catch(Exception e){
		}
	}
	
	
	 
	
	 @Override
     public void onRequestResult(boolean result, JSONObject data, int requestCode) {
         if(result) {
             try {
                     String status = data.getString("msg");
                     if(status.equals("ok")) {  
                    	 Intent i = new Intent();
                    	 i.putExtra("hasAccountTemp", true);
                    	 i.putExtra("authTokenTemp", data.getString("token"));
                    	 setResult(Activity.RESULT_OK, i);
	                     finish();
                     } else if(status.equals("Username taken")){
                         Toast.makeText(getApplicationContext(),"Username already taken or device already has an account created.", Toast.LENGTH_SHORT).show();   
                     }
             } catch (Exception e) {
                     Log.e("Req_tag", "Error getting result.", e);
                     Toast.makeText(getApplicationContext(),
                                     "A problem was encountered. Pleasy try again later.", 
                                     Toast.LENGTH_SHORT).show();
             }
         } else {
                 Log.e("Req_tag", "Request failed.");
                 Toast.makeText(getApplicationContext(),
                                 "A problem was encountered. Pleasy try again later.", 
                                 Toast.LENGTH_SHORT).show();
         }
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
