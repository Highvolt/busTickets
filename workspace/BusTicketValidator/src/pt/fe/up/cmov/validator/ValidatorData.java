package pt.fe.up.cmov.validator;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;


public enum ValidatorData {
	INSTANCE;
	private  ValidatorDatabaseHelper db=null;
	private String pubKey="";
	private String privKey="";
	public int id=0;
	protected String key;
	private ValidatorData(){
		db=new ValidatorDatabaseHelper(App.getAppContext());
		long existsKey=db.getReadableDatabase().compileStatement("Select count(*) from keys where creationDate=date('now');").simpleQueryForLong();
		if(existsKey<=0){
			genKeys();
		}else{
			Cursor a=db.getReadableDatabase().query("Keys",new String[] {"privKey","pubKey"}, "creationDate=date('now')", new String[]{}, "", "", "");
			a.moveToNext();
			if(a.moveToFirst()){
				privKey=a.getString(0);
				pubKey=a.getString(1);
			}
		}
		
	}
	
	private void genKeys() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(512);
			KeyPair keys=keyGen.generateKeyPair();
			String publick=Base64.encodeToString(keys.getPublic().getEncoded(), Base64.DEFAULT);
			String privatek=Base64.encodeToString(keys.getPrivate().getEncoded(), Base64.DEFAULT);
			
			SQLiteDatabase db = this.db.getWritableDatabase();
			SQLiteStatement stmt = db.compileStatement("Insert into keys (creationDate,privKey,pubKey) values (date('now'),?,?);");
			stmt.bindString(1, privatek);
			stmt.bindString(2, publick);
			stmt.execute();
			privKey=privatek;
			pubKey=publick;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
