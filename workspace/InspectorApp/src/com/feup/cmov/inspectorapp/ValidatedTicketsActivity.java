package com.feup.cmov.inspectorapp;

import com.feup.cmov.inspectorapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ValidatedTicketsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ListView list;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validated_tickets);
		list=(ListView)findViewById(R.id.validatedTicketsList);
		//list.setAdapter(new ArrayAdapter<String>(this,R.layout.mytextview ,list_array));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
