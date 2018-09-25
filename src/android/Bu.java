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
import android.util.Log;

public class Bu extends CordovaPlugin {

    final private String TAG = "EADES BU PLUGIN";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        final CallbackContext _callbackContext = callbackContext;
        final Context _context = this.cordova.getActivity().getApplicationContext();
       
        if (action.equals("startBu")) {
            // configs
            JSONObject config = data.getJSONObject(0);
            String API_KEY = config.getString("API_KEY");

            // Server Errors
            ServerErrorHandler errorHandler = new ServerErrorHandler() {
                @Override
                public void onError(String errorMsg) {
                    Log.e(TAG, "ServerErrorHandler : ERROR : " + errorMsg);
                    Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            };

            // Token status
            final TokenStatusCallback tokenStatusCallback = new TokenStatusCallback() {

                @Override public void onSuccess(JSONObject json) {
                    Log.i(TAG, "TokenStatusCallback : SUCCESS : " + json.toString());
                    // return to callback
                    _callbackContext.success(json);
                }

                @Override public void onError(String errorMsg) {
                    Log.e(TAG, "TokenStatusCallback : ERROR : " + errorMsg);
                    Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
                }

                @Override public void onTimeout() {
                    Log.e(TAG, "TokenStatusCallback : Timed out");
                    Toast.makeText(_context, "Timed out", Toast.LENGTH_SHORT).show();
                }
            };

            // Create instance
            final BlinkupController blinkup = BlinkupController.getInstance();
            // Acquire Token
            blinkup.acquireSetupToken(this.cordova.getActivity(), API_KEY, new TokenAcquireCallback() {
                @Override
                public void onSuccess(String planID, String token) {
                    Log.i(TAG, "TokenAcquireCallback : SUCCESS ---> plan ID : " + planID + " ; token : " + token);
                    blinkup.setPlanID(planID);
                    blinkup.getTokenStatus(token, tokenStatusCallback);
                }
                @Override
                public void onError(String errorMsg) {
                    Log.e(TAG, "TokenAcquireCallback : ERROR : " + errorMsg);
                    Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
            
            // Show network list
            blinkup.selectWifiAndSetupDevice(this.cordova.getActivity(), API_KEY, errorHandler);

            return true;

        } else {
            
            return false;

        }
    }
}
