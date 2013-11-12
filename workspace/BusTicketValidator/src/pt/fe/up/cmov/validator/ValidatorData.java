package pt.fe.up.cmov.validator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
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
	private Certificate serverPub=null;
	
	public String signTicket(JSONObject obj){
		
		try {
			Signature sig = Signature.getInstance("SHA1WithRSA");
			KeyFactory fact=KeyFactory.getInstance("RSA");
			 byte[] clear = Base64.decode(privKey, Base64.DEFAULT);
			 PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
			sig.initSign(fact.generatePrivate(keySpec));
			Arrays.fill(clear, (byte) 0);
			String data=obj.getString("time")+"-"+obj.getString("device")+"-"+obj.getString("useTime")+"-"+obj.getString("type")+"-"+obj.getString("BusId");
			
			sig.update(data.getBytes());
			byte[] signature=sig.sign();
			return new String(Base64.encode(signature,Base64.NO_WRAP));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	public boolean validateTicket(JSONObject obj){
		try {
			String data=""+obj.getString("user")+'-'+obj.getString("type")+'-'+obj.getString("time");
			Signature sig = Signature.getInstance("SHA256WithRSA");
			sig.initVerify(this.serverPub);
			sig.update(data.getBytes());
			return sig.verify(Base64.decode(obj.getString("signature").getBytes(), Base64.NO_WRAP));
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public void loadServerPub(Context c){
		
		try {
			 CertificateFactory a= CertificateFactory.getInstance("X.509");
			 serverPub=a.generateCertificate(c.getAssets().open("publickey.cer"));
			
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
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
