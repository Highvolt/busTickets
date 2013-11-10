package pt.fe.up.cmov.busticket.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.fe.up.cmov.busticket.client.APIRequestTask.HttpRequestType;



import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BuyActivity extends Activity implements RequestResultCallback {
	
	public static final String GET_PRICE_URL = "/getPrice";
	public static final String BUY_URL = "/buy_ticket";
	public static final int REQCODE_PRICES = 104;
	public static final int REQCODE_BUY = 105;
	public String authTokenTemp = "";

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
		
		Button buyButton = (Button) findViewById(R.id.BuyButton);
		buyButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int t1quantity = Integer.parseInt(t1Value.getText().toString().trim());
				int t2quantity = Integer.parseInt(t2Value.getText().toString().trim());
				int t3quantity = Integer.parseInt(t3Value.getText().toString().trim());
				
				
				SharedPreferences prefs = getSharedPreferences("user_details", MODE_PRIVATE);
				String authToken = prefs.getString("authToken", "");
				authTokenTemp = authToken;
				
				JSONObject data = new JSONObject();
				try {
					data.put("t1", t1quantity);
					data.put("t2", t2quantity);
					data.put("t3", t3quantity);
					data.put("key", authToken);
					
					APIRequestTask request = new APIRequestTask(BuyActivity.this, HttpRequestType.Post, data, 
							MainMenuActivity.SERVER_ADDRESS + GET_PRICE_URL, "Retrieving prices", REQCODE_PRICES);
					request.execute((Void[]) null);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	private void buildBuyAlertDialog(final int t1, final int t2, final int t3, int t1gift,
			int t2gift, int t3gift, double total, double[] prices) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Buy tickets");
        String offered = "";
        if(t1gift != 0 || t2gift != 0 || t3gift != 0)
        	offered += "Tickets offered";
        if(t1gift != 0){
        	offered += "- " + t1gift + " T1 ";
        }
        if(t2gift != 0){
        	offered += "- " + t2gift + " T2 ";
        }
        if(t3gift != 0){
        	offered += "- " + t3gift + " T3 ";
        }
        alertDialog.setMessage(
        		t1 + " Type 1 Tickets: " + prices[0] + "� \n" +
        		t2 + " Type 2 Tickets: " + prices[1] + "� \n" +
        		t3 + " Type 3 Tickets: " + prices[2] + "� \n" +
        		offered + "\n" +
        		"Price: " + total + "�." +
        		"\n Do you wanna buy these tickets?");		
     // Back button.
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {}
        });

        // Ok button.
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {    
                    JSONObject json = new JSONObject();
                    json.put("t1", t1);
                    json.put("t2", t2);
                    json.put("t3", t3);
                    json.put("key", authTokenTemp);
                    APIRequestTask task = new APIRequestTask(BuyActivity.this, 
                        HttpRequestType.Post, json, 
                        MainMenuActivity.SERVER_ADDRESS + BUY_URL, 
                        "Buying tickets.", REQCODE_BUY);
                    task.execute((Void[]) null);
                } catch (Exception e) {}
            }
        });
        alertDialog.show();
	}
	
	@Override
	public void onRequestResult(int responseCode, boolean result, JSONObject data, int requestCode) {
		if(result) {
			if(requestCode == REQCODE_PRICES){
				try {
					if(responseCode == 400 || responseCode == 500){
						Log.e("Req_tag", "Request failed.");
			             Toast.makeText(getApplicationContext(),
			                             "An authentication error has occured.", 
			                             Toast.LENGTH_SHORT).show();
			             finish();
					}else{
						int t1, t2, t3, t1gift, t2gift, t3gift = 0;
						double total = 0.0;
						double[] prices = new double[3];
						
						t1 = data.getInt("t1");
						t2 = data.getInt("t2");
						t3 = data.getInt("t3");
						t1gift = data.getInt("t1_gift");
						t2gift = data.getInt("t2_gift");
						t3gift = data.getInt("t3_gift");
						total = data.getDouble("total");
						JSONArray array = data.getJSONArray("prices");
						
						prices[0] = array.getDouble(0);
						prices[1] = array.getDouble(1);
						prices[2] = array.getDouble(2);
						
						buildBuyAlertDialog(t1,t2,t3,t1gift,t2gift,t3gift,total,prices);
						
					}
				} catch (Exception e) {
					Log.e("Req_tag", "Error getting result.", e);
                    Toast.makeText(getApplicationContext(),
                                    "A problem was encountered. Pleasy try again later.", 
                                    Toast.LENGTH_SHORT).show();
				}
			}else if(requestCode == REQCODE_BUY){
				try {
					JSONArray tickets = data.getJSONArray("tickets");
					if(tickets != null){
						DatabaseHandler db = new DatabaseHandler(this);
						for(int i = 0; i< tickets.length(); i++){
							Ticket t = new Ticket((JSONObject) tickets.get(i));
							db.addTicket(t);
						}
						
						Toast.makeText(getApplicationContext(),
                                "Tickets acquired.", 
                                Toast.LENGTH_SHORT).show();
					}
				}catch(Exception e){
					Log.e("Req_tag", "Error getting result.", e);
                    Toast.makeText(getApplicationContext(),
                                    "A problem was encountered. Pleasy try again later.", 
                                    Toast.LENGTH_SHORT).show();
				}
			}
		}else{
			 Log.e("Req_tag", "Request failed.");
             Toast.makeText(getApplicationContext(),
                             "A problem was encountered. Pleasy try again later.", 
                             Toast.LENGTH_SHORT).show();
		}
		
	}

}
