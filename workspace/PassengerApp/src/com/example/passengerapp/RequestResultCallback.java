package com.example.passengerapp;

import org.json.JSONObject;

public interface RequestResultCallback {
	public void onRequestResult(boolean result, JSONObject data, int requestCode);
}
