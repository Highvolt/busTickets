package pt.fe.up.cmov.validator;

import pt.fe.up.cmov.Misc;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class BusValidator extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.setAppContext(getApplicationContext());
		setContentView(R.layout.activity_bus_validator);
		Misc.INSTANCE.setDevID(Secure.getString(this.getApplicationContext().getContentResolver(),  Secure.ANDROID_ID));
		
		TextView t=(TextView)findViewById(R.id.idAutocarro);
		t.setText(Misc.INSTANCE.getDevId());
		ValidatorData db=ValidatorData.INSTANCE;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bus_validator, menu);
		return true;
	}
	
}
