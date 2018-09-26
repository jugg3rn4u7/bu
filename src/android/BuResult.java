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

public class BuResult extends Activity {
    final private String TAG = "EADES BU PLUGIN";
    private BlinkupController blinkup;
    private CallbackContext callbackContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blinkup = BlinkupController.getInstance();
        callbackContext = this.getIntent().getObjectExtra("callbackContext");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        blinkup.handleActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Context _context = this.getApplicationContext();

        blinkup.getTokenStatus(new TokenStatusCallback() {

            @Override public void onSuccess(JSONObject json) {
                Log.i(TAG, "TokenStatusCallback : SUCCESS : " + json.toString());
                // return to callback
                callbackContext.success(json);

                // PluginResult pluginResult = new PluginResult(cordovaResultStatus, resultJSON.toString());
                // pluginResult.setKeepCallback(true); // uses same BlinkUpPlugin object across calls, so need to keep callback
                // BlinkUpPlugin.getCallbackContext().sendPluginResult(pluginResult);
            }

            @Override public void onError(String errorMsg) {
                Log.e(TAG, "TokenStatusCallback : ERROR : " + errorMsg);
                Toast.makeText(_context, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override public void onTimeout() {
                Log.e(TAG, "TokenStatusCallback : Timed out");
                Toast.makeText(_context, "Timed out", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        blinkup.cancelTokenStatusPolling();
    }
}