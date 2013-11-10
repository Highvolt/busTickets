package pt.fe.up.cmov.busticket.client;

import org.json.JSONObject;

public interface RequestResultCallback {
	public void onRequestResult(int responseCode, boolean result, JSONObject data, int requestCode);
}
