package com.example.passengerapp;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	
	private int pickerYear = 0;
    private int pickerMonth = 0;
    private int pickerDay = 0;
    
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
