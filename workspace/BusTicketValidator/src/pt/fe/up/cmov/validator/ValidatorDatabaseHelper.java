package pt.fe.up.cmov.validator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ValidatorDatabaseHelper extends SQLiteOpenHelper {
	
	public ValidatorDatabaseHelper(Context context) {
		super(context, "vdb.db", null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	private static final int DATABASE_VERSION = 2;
   
    private static final String TICKET_TABLE_CREATE =
    		"Create Table Ticket (" +
					"type integer not null," +
					"userid integer not null," +
					"devid varchar not null," +
					"usedate datetime not null," +
					"Primary key (userid,usedate)" +
					");";
    private static final String KEYS_TABLE_CREATE =
    		"Create Table Keys (" +
					"creationDate date not null Primary Key," +
					"privKey varchar not null," +
					"pubKey varchar not null" +
					");";
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
		arg0.execSQL(TICKET_TABLE_CREATE);
		
		arg0.execSQL(KEYS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion!=newVersion){
			db.execSQL("Drop if exists ticket;");
			db.execSQL("Drop if exists keys;");
			onCreate(db);
		}
	}

}
