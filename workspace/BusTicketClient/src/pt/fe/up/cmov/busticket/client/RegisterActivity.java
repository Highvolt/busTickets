package pt.fe.up.cmov.busticket.client;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONObject;

import pt.fe.up.cmov.busticket.client.APIRequestTask.HttpRequestType;
import pt.fe.up.cmov.busticket.client.APIRequestTask;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	
	private int pickerYear = 2013;
    private int pickerMonth = 11;
    private int pickerDay = 1;
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
		
		GregorianCalendar dob = new GregorianCalendar();
		pickerDay=1;
		pickerYear=dob.get(Calendar.YEAR);
		pickerMonth=dob.get(Calendar.MONTH);
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
		 boolean internet=false;
		 NetworkInfo info = (NetworkInfo) ((ConnectivityManager) getApplicationContext()
				 .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		 if (info == null || !info.isConnected()) {
		        internet = false;
		    }else if (info.isRoaming()) {
			        // here is the roaming option you can change it if you want to
			        // disable internet while roaming, just return false
			    	internet = false;
			    }else{
			    	internet =  true;
			 }
		    
		    
		    if(!internet){
		    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Exit");
                alertDialog.setMessage("Necessita de ligação à Internet para entrar na aplicação. " +
                                "Por favor active uma ligação.");
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
					Toast.makeText(getApplicationContext(), "Nome de Utilizador demasiado curto.", Toast.LENGTH_SHORT).show();
					return;
				}else if(nameField.length() > 25){
					Toast.makeText(getApplicationContext(), "Nome de Utilizador demasiado longo.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String passField = password.getText().toString().trim();
				if(passField.length() < 5){
					Toast.makeText(getApplicationContext(), "Palavra-passe demasiado curta.", Toast.LENGTH_SHORT).show();
					return;
				}else if(passField.length() > 30){
					Toast.makeText(getApplicationContext(), "Palavra-passe demasiado longa.", Toast.LENGTH_SHORT).show();
					return;
				}else if(passField.contains(" ")){
					Toast.makeText(getApplicationContext(), "Palavra-passe não pode conter espaços.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String ccnField = ccn.getText().toString().trim();
				if(ccnField.length() != 16){
					Toast.makeText(getApplicationContext(), "O nº de cartão de crédito deve conter 16 dígitos.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String cccodeField = cc_csc.getText().toString().trim();
				if(cccodeField.length() != 3){
					Toast.makeText(getApplicationContext(), "O código de segurança deve conter 3 dígitos.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String ccdateField = ccdate.getText().toString().trim();
				Calendar c = Calendar.getInstance();
				
				if(ccdateField.isEmpty()){
					Toast.makeText(getApplicationContext(), "Introduza data de validade do cartão.", Toast.LENGTH_SHORT).show();
					return;
				}else if(pickerYear < c.get(Calendar.YEAR)){
					Toast.makeText(getApplicationContext(), "O cartão actual encontra-se expirado.", Toast.LENGTH_SHORT).show();
					return;
				}else if(pickerYear == c.get(Calendar.YEAR) && pickerMonth < c.get(Calendar.MONTH)){
					Toast.makeText(getApplicationContext(), "O cartão actual encontra-se expirado.", Toast.LENGTH_SHORT).show();
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
     public void onRequestResult(int responseCode, boolean result, JSONObject data, int requestCode) {
         if(result) {
             try {
                     if(data.has("token")){
                    	 Intent i = new Intent();
                    	 i.putExtra("hasAccountTemp", true);
                    	 i.putExtra("authTokenTemp", data.getString("token"));
                    	 setResult(Activity.RESULT_OK, i);
	                     finish();
                     } else if(data.has("msg")){
                    	 String message = data.getString("msg");
                    	 if(message.equals("Bad Auth information"))
                    		 Toast.makeText(getApplicationContext(),"Já existe uma conta associada ao nome de utilizador introduzido ou ao dispositivo utilizado.", Toast.LENGTH_SHORT).show();
                    	 else
                    		 Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                     }
             } catch (Exception e) {
                     Log.e("Req_tag", "Error getting result.", e);
                     Toast.makeText(getApplicationContext(),
                                     "Foi encontrado um problema. Por favor tente mais tarde.", 
                                     Toast.LENGTH_SHORT).show();
             }
         } else {
                 Log.e("Req_tag", "Request failed.");
                 Toast.makeText(getApplicationContext(),
                                 "Foi encontrado um problema. Por favor tente mais tarde.", 
                                 Toast.LENGTH_SHORT).show();
         }
     }
	 
	 @Override
    public void onBackPressed() {
		Intent intent = new Intent();
		setResult(Activity.RESULT_CANCELED, intent);
		finish();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
