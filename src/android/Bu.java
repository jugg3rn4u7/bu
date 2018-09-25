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
import android.widget.Toast;

public class Bu extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        final Context _context = this.cordova.getActivity().getApplicationContext();
       
        if (action.equals("startBu")) {
            // configs
            JSONObject config = data.getJSONArray(0);
            String API_KEY = config.getString("API_KEY");

            // Server Errors
            ServerErrorHandler errorHandler = new ServerErrorHandler() {
                @Override
                public void onError(String errorMsg) {
                    Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            };

            // Create instance
            BlinkupController blinkup = BlinkupController.getInstance();
            // Acquire Token
            blinkup.acquireSetupToken(this, API_KEY, new TokenAcquireCallback() {
                @Override
                public void onSuccess(String planID, String token) {
                    blinkup.setPlanID(planID);
                }
                @Override
                public void onError(String errorMsg) {
                    Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
            
            // Show network list
            blinkup.selectWifiAndSetupDevice(this, API_KEY, errorHandler);

            // Token status
            TokenStatusCallback tokenStatusCallback = new TokenStatusCallback() {

                @Override public void onSuccess(JSONObject json) {
                    // return to callback
                    callbackContext.success(json);
                }

                @Override public void onError(String errorMsg) {
                    Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
                }

                @Override public void onTimeout() {
                    Toast.makeText(_context, "Timed out", Toast.LENGTH_SHORT).show();
                }
            };

            return true;

        } else {
            
            return false;

        }
    }
}
