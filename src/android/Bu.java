package com.eades.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.electricimp.blinkup.BlinkupController;
import com.electricimp.blinkup.TokenStatusCallback;
import com.electricimp.blinkup.TokenAcquireCallback;
import com.electricimp.blinkup.ServerErrorHandler;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

public class Bu extends CordovaPlugin {

    final private String TAG = "EADES BU PLUGIN";
    private static CallbackContext sCallbackContext;
    private CordovaInterface cordova;
    private BlinkupController blinkup;

    @Override 
    public void initialize (CordovaInterface initCordova, CordovaWebView webView) {
        Log.i(TAG, "initialize");
        cordova = initCordova;
        super.initialize (cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        
        sCallbackContext = callbackContext;
        final Context _context = this.cordova.getActivity().getApplicationContext();
       
        if (action.equals("startBu")) {
            // configs
            JSONObject config = data.getJSONObject(0);
            String API_KEY = config.getString("API_KEY");

            Log.i(TAG, "API_KEY : " + API_KEY);
            Log.i(TAG, "Configuration : " + config.toString());

            // Create instance
            blinkup = BlinkupController.getInstance();

            // Show network list
            blinkup.selectWifiAndSetupDevice(this.cordova.getActivity(), API_KEY, new ServerErrorHandler() {
                @Override
                public void onError(String errorMsg) {
                    Log.e(TAG, "ServerErrorHandler : ERROR : " + errorMsg);
                    Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });

            // Assign blink up complete intent
            blinkup.intentBlinkupComplete = new Intent(_context, BuResult.class);
            
            return true;
        } 
        
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        blinkup.handleActivityResult(this.cordova.getActivity(), requestCode, resultCode, data);
    }

    static CallbackContext getCallbackContext() {
        return sCallbackContext;
    }
}
