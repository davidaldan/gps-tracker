package com.gps.plugin;

import java.util.HashMap;
import java.util.Map;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;
//import android.telephony.TelephonyManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.GeofenceStatusCodes;
//import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

//import io.ionic.hotelmanager.R;

public class GeolocationService extends Service implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener, ResultCallback<Status> {
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 20000;  //10000 = 1s
	public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 5;
	protected GoogleApiClient mGoogleApiClient;
	protected LocationRequest mLocationRequest;
  private static final String TAG = "LocationService";
  String deviceId;
	private PendingIntent mPendingIntent;

	@Override
	public void onStart(Intent intent, int startId) {
		buildGoogleApiClient();

		/*mGoogleApiClient.connect();*/
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}

	}


	public void broadcastLocationFound(Location location) {

    SharedPreferences sharedPreferences = this.getSharedPreferences("com.gps.plugin.prefs", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    final RequestParams requestParams = new RequestParams();
    requestParams.put("latitude", Double.toString(location.getLatitude()));
    requestParams.put("longitude", Double.toString(location.getLongitude()));
    requestParams.put("locationmethod", location.getProvider());
    requestParams.put("fcm_token", sharedPreferences.getString("fcm", ""));
    requestParams.put("hotel", sharedPreferences.getString("hotel", ""));
    requestParams.put("uuid", sharedPreferences.getString("uuid", ""));
    requestParams.put("reservation", sharedPreferences.getString("reservation", ""));

    final String uploadWebsite = "http://www.api.muchomil.com/hotelmanager/gps";

    Log.i(TAG, requestParams.toString());

    LoopjHttpClient.get(uploadWebsite, requestParams, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
        LoopjHttpClient.debugLoopJ(TAG, "sendLocationDataToWebsite - success", uploadWebsite, requestParams, responseBody, headers, statusCode, null);

        //stopSelf();
      }
      @Override
      public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
        LoopjHttpClient.debugLoopJ(TAG, "sendLocationDataToWebsite - failure", uploadWebsite, requestParams, errorResponse, headers, statusCode, e);
        //stopSelf();
      }
    });

	}

	protected void startLocationUpdates() {
    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
      || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    {
    LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
    }
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "Connected to GoogleApiClient");

		startLocationUpdates();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG,
				"new location : " + location.getLatitude() + ", "
						+ location.getLongitude() + ". "
						+ location.getAccuracy());
		broadcastLocationFound(location);

		//if (!MainActivity.geofencesAlreadyRegistered) {
			//registerGeofences();
		//}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG,
				"Connection failed: ConnectionResult.getErrorCode() = "
						+ result.getErrorCode());
	}

	protected synchronized void buildGoogleApiClient() {
		Log.i(TAG, "Building GoogleApiClient");
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
    mGoogleApiClient.connect();
    createLocationRequest();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest
				.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onResult(Status status) {
		if (status.isSuccess()) {
			/*Toast.makeText(getApplicationContext(),
					getString(R.string.geofences_added), Toast.LENGTH_SHORT)
					.show();*/
		} else {
			/*MainActivity.geofencesAlreadyRegistered = false;
			String errorMessage = getErrorString(this, status.getStatusCode());
			Toast.makeText(getApplicationContext(), errorMessage,
					Toast.LENGTH_LONG).show();*/
		}
	}

	/*public static String getErrorString(Context context, int errorCode) {
		Resources mResources = context.getResources();
		switch (errorCode) {
		case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
			return mResources.getString(R.string.geofence_not_available);
		case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
			return mResources.getString(R.string.geofence_too_many_geofences);
		case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
			return mResources
					.getString(R.string.geofence_too_many_pending_intents);
		default:
			return mResources.getString(R.string.unknown_geofence_error);
		}
	}*/

}
