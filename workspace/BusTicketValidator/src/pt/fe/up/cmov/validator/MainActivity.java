package pt.fe.up.cmov.validator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends Activity {
	QRCodeEncoder qrCodeEncoder = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
	        Display display = manager.getDefaultDisplay();
	   int width = display.getWidth();
	   int height = display.getHeight();
	   int smallerDimension = width < height ? width : height;
	   smallerDimension = smallerDimension * 7 / 8;
		
		qrCodeEncoder = new QRCodeEncoder(BluetoothAdapter.getDefaultAdapter().getAddress(), null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), smallerDimension);
		Bitmap bitmap=null;
		try {
			bitmap = qrCodeEncoder.encodeAsBitmap();
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bitmap==null){
			Log.d("MainActivity","QR is null");
		}
        ImageView view = (ImageView) findViewById(R.id.imageView1);
        view.setImageBitmap(bitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
