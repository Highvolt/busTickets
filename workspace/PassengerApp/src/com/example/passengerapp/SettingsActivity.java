package com.example.passengerapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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
		String list_array[]={"Credit Card","E-mail","Password"};
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		list=(ListView)findViewById(R.id.listView1);
		list.setAdapter(new ArrayAdapter<String>(this,R.layout.mytextview ,list_array));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				if(position == 2){
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				       final View layout = inflater.inflate(R.layout.change_password, (ViewGroup) findViewById(R.id.root));
				       final EditText password1 = (EditText) layout.findViewById(R.id.EditText_Pwd1);
				       final EditText password2 = (EditText) layout.findViewById(R.id.EditText_Pwd2);
				       final TextView error = (TextView) layout.findViewById(R.id.TextView_PwdProblem);
				       password2.addTextChangedListener(new TextWatcher() {
				    	   public void afterTextChanged(Editable s) {
				    	      String strPass1 = password1.getText().toString();
				    	      String strPass2 = password2.getText().toString();
				    	      if (strPass1.equals(strPass2)) {
				    	         error.setText("Passwords match");
				    	      } else {
				    	         error.setText("Passwords don't match");
				    	      }
				    	   }
				    	   public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				    	   public void onTextChanged(CharSequence s, int start, int before, int count) {}
				    	 });
				       AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
				       builder.setTitle("Enter Password");
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
				    	            "Matching passwords="+strPassword2, Toast.LENGTH_SHORT).show();
				    	      }
				    	      removeDialog(MY_PASSWORD_DIALOG_ID);
				    	   }
				    	});
				    	builder.show();
				       
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
