package pt.fe.up.cmov.busticket.inspector;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class APIRequestTask extends AsyncTask<Void, Void, Void>{

	public static enum HttpRequestType {
        Get, Post
	}
	
	private ProgressDialog progressDialog = null;
	private String progressMessage = null;
	
    private Activity callingActivity = null;
    
    private HttpRequestType requestType = HttpRequestType.Get;
    private JSONObject requestData = null;
    private JSONObject requestResult = null;
    private String requestUrl = null;
    private int requestCode = 0;
    private int responseCode = 0;
    
    public APIRequestTask(RequestResultCallback callingActivity, HttpRequestType requestType, JSONObject requestData, String requestUrl, 
    		String progressMessage,int requestCode) {
	    this.callingActivity = (Activity) callingActivity;
	    this.requestType = requestType;
	    this.requestData = requestData;
	    this.requestUrl = requestUrl;
	    this.progressMessage = progressMessage;
	    this.requestCode = requestCode;
	    this.progressDialog = new ProgressDialog(this.callingActivity);
    }
	
	@Override
	protected Void doInBackground(Void... params) {
		HttpContext localContext = new BasicHttpContext();
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        if(this.requestType == HttpRequestType.Post) {
            try {
                HttpPost post = new HttpPost(new URI(this.requestUrl));
                post.setHeader("Content-type", "application/json");
                post.setEntity(new StringEntity(this.requestData.toString(), "UTF-8"));
                response = client.execute(post, localContext);
            } catch (Exception e) {
                Log.e("Req Tag", "POST request failed.", e);
            }
        } else {
            try {
                HttpGet get = new HttpGet(new URI(this.requestUrl));
                response = client.execute(get, localContext);
            } catch (Exception e) {
                Log.e("Req Tag", "GET request failed.", e);
            }
        }
        if(response != null) {
            try {
                this.requestResult = new JSONObject(EntityUtils.toString(response.getEntity()));
                this.responseCode = response.getStatusLine().getStatusCode();
            } catch (Exception e) {
                Log.e("Req Tag", "No response obtained.", e);
            }
        }
        return null;
	}
	
	@Override
    protected void onPreExecute() {
        progressDialog.setMessage(this.progressMessage);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
	
	@Override
    protected void onPostExecute(Void result) {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
        }
        ((RequestResultCallback) callingActivity).onRequestResult(responseCode, requestResult != null, requestResult, requestCode);
    }

	
}
