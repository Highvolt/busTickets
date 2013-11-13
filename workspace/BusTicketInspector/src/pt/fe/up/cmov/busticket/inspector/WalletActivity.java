package pt.fe.up.cmov.busticket.inspector;

import pt.fe.up.cmov.busticket.inspector.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WalletActivity extends Activity {
	int t1quantity = 0;
	int t2quantity = 0;
	int t3quantity = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		
		DatabaseHandler db = new DatabaseHandler(this);
		int[] ticketCount = db.getNotValidatedTicketsCount();
		
		t1quantity = ticketCount[0];
		t2quantity = ticketCount[1];
		t3quantity = ticketCount[2];
		
		TextView t1 = (TextView) findViewById(R.id.walletT1Label);
		TextView t2 = (TextView) findViewById(R.id.walletT2Label);
		TextView t3 = (TextView) findViewById(R.id.walletT3Label);
		
		t1.setText(String.valueOf(t1quantity));
		t2.setText(String.valueOf(t2quantity));
		t3.setText(String.valueOf(t3quantity));
		
		
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
