package pt.fe.up.cmov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestClient {
	public static String APIurl="http://cmov.inphormatic.us/";
	public static String intentAction="pt.up.fe.cmov.rest";
	public int status=-1;
	public String result=null;
	private String url;
	private JSONObject data;
	public static Context appContext=null;
	
	public RestClient(String url,JSONObject data){
		this.url=url;
		this.data=data;
		Log.d(getClass().getName(),url);
	}
	
	public JSONObject toJSON(){
		JSONObject obj=new JSONObject();
		try {
			obj.put("status",this.status);
			obj.put("result", this.result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	public RestClient connect()
	{
		Log.d(getClass().getName(),"request: "+url);
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
	    HttpClient httpclient = new DefaultHttpClient(httpParams);
	    
	    HttpPost httppost = new HttpPost(url); 
	    httppost.setHeader("Content-Type","application/json");
	    try {
			httppost.setEntity(new StringEntity(data.toString()));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httppost);
	        this.status=response.getStatusLine().getStatusCode();
	        Log.d(getClass().getName(),"request: "+url+" resposta: "+status);
	        HttpEntity entity = response.getEntity();

	        if (entity != null) {

	            
	            InputStream instream = entity.getContent();
	            this.result= convertStreamToString(instream);
	            instream.close();
	        }


	    } catch (Exception e) {
	    	this.status=-1;
	    }
	    if(appContext!=null){
	    	Intent intent=new Intent(RestClient.intentAction);
	    	intent.putExtra("data", this.toJSON().toString());
	    	appContext.sendBroadcast(intent);
	    }
	    
	    return this;
	}
	
	public JSONObject getAsJSONObject() throws JSONException{
		if(this.result==null){
			return null;
		}else{
			JSONObject obj=new JSONObject(result);
			return obj;
			
		}
	}
	
	public JSONArray getAsJSONArray() throws JSONException{
		if(this.result==null){
			return null;
		}else{
			JSONArray obj=new JSONArray(result);
			return obj;
			
		}
	}

	private String convertStreamToString(InputStream is) {
	    
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	
}
