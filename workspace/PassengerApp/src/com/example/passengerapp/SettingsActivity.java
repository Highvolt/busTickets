package com.example.passengerapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	private static final int MY_PASSWORD_DIALOG_ID = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ListView list;
		String list_array[]={"Credit Card","Password"};
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		list=(ListView)findViewById(R.id.listView1);
		list.setAdapter(new ArrayAdapter<String>(this,R.layout.mytextview ,list_array));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				if(position == 0){
					createCreditCardDialog();			       
				}
				if(position == 1){
					createPasswordDialog();			       
				}				
			}
		});
	}

	public void createCreditCardDialog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void createPasswordDialog(){
		   LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	       final View layout = inflater.inflate(R.layout.change_password, (ViewGroup) findViewById(R.id.root));
	       final EditText password1 = (EditText) layout.findViewById(R.id.EditText_Pwd1);
	       final EditText password2 = (EditText) layout.findViewById(R.id.EditText_Pwd2);
	       final TextView error = (TextView) layout.findViewById(R.id.TextView_PwdProblem);
	       
	       AlertDialog.Builder builder = new AlertDialog.Builder(this);
	       builder.setTitle("Change Password");
	       builder.setView(layout);
	       builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	    	   public void onClick(DialogInterface dialog, int whichButton) {
	    	      removeDialog(MY_PASSWORD_DIALOG_ID);
	    	   }
	    	});
	    	builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	    	   public void onClick(DialogInterface dialog, int which) {
	    	      String strPassword1 = password1.getText().toString();
	    	      String strPassword2 = password2.getText().toString();
	    	      if (strPassword1.equals(strPassword2)) {
	    	         Toast.makeText(SettingsActivity.this,
	    	            "Updating Password", Toast.LENGTH_LONG).show();
	    	         //TODO Fazer cenas
	    	         
	    	         removeDialog(MY_PASSWORD_DIALOG_ID);
	    	      }
	    	      else{
	    	    	  error.setText("Passwords don't match");
	    	    	  error.setTextColor(0xff0000);
	    	      }
	    	      
	    	   }
	    	});
	    	builder.show();
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
