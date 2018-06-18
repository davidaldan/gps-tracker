package com.gps.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
//import java.util.UUID;

public class GpsTracker extends CordovaPlugin {
  private static final String TAG = "LocationService";
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    if ("echo".equals(action)) {
      echo(args.getString(0), callbackContext);
      return true;
    }

    if ("startService".equals(action)) {
        Context context= cordova.getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.gps.plugin.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        HashMap<String, String> jsonMap = new HashMap<String, String>();
        JSONArray jsonArray= new JSONArray(args.toString());
        JSONObject jsonObj = jsonArray.getJSONObject(0);
        String fcm = jsonObj.getString("fcm");
        String hotel = jsonObj.getString("hotel");
        String uuid = jsonObj.getString("uuid");
        String reservation = jsonObj.getString("reservation");

        editor.putString("fcm",  fcm );
        editor.putString("hotel",  hotel );
        editor.putString("uuid",  uuid );
        editor.putString("reservation",  reservation );
        editor.commit();
        Log.i(TAG, fcm);
        Log.i(TAG, hotel);
        Log.i(TAG, "GeolocationService Intent");

        Intent intent=new Intent(context,GeolocationService.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
        callbackContext.success();
        return true;
    }

    if ("stopService".equals(action)) {
        return true;
    }

    if ("getCoors".equals(action)) {
        CallbackContext context;
        context = callbackContext;
        Context contextx= cordova.getActivity().getApplicationContext();
        Log.d(TAG, "GPS getCoors in getCoors");
        GPSLocation gps = new GPSLocation(contextx);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        JSONObject coors = new JSONObject();
        //coors.put(String.valueOf('latitude'),String.valueOf(latitude));
        coors.put(String.valueOf(1), String.valueOf(latitude));
        coors.put(String.valueOf(2), String.valueOf(longitude));
        context.sendPluginResult(new PluginResult(PluginResult.Status.OK, coors));
        return true;
    }

    return false;
  }

  /*private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
    ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        Log.i("Service already","running");
        return true;
      }
    }
    Log.i("Service not","running");
    return false;
  }*/

  private void echo(String msg, CallbackContext callbackContext) {
    if (msg == null || msg.length() == 0) {
      callbackContext.error("Empty message!");
    } else {
      Toast.makeText(
        webView.getContext(),
        msg,
        Toast.LENGTH_LONG
      ).show();
      callbackContext.success(msg);
    }
  }
}






