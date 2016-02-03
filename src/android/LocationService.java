package com.tcloud.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import com.tcloud.util.Constantes;
import com.tcloud.util.CoreDB;

import org.apache.http.Header;
import org.json.JSONObject;
import org.json.JSONArray;

public class LocationService extends Service implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {


    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    private static String TAG = "LocationService";

    private String url = Constantes.URL + "/api/v1/location";
    private RequestParams params;
    private CoreDB DB;

    @Override
    public void onStart(Intent intent, int startId) {

        Log.e(TAG, "onStart GoogleApiClient");

        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    public void broadcastLocationFound(Location location) {

        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        params = new RequestParams();
        params.put("UID", deviceId);
        params.put("LAT", Double.toString(location.getLatitude()));
        params.put("LON", Double.toString(location.getLongitude()));
        params.put("ACU", Double.toString(location.getAccuracy()));
        params.put("SPD", Double.toString(location.getSpeed()));

        PersistentCookieStore CookieStore = new PersistentCookieStore(
                LocationService.this);
        CookieStore.clear();
        AsyncHttpClient sendLocation = new AsyncHttpClient(false, 80, 443);
        sendLocation.setCookieStore(CookieStore);
        sendLocation.setTimeout(60000);
        sendLocation.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, response.toString());
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, response.toString());
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Log.i(TAG, responseString);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.v(TAG, responseString,throwable);
            }


        });
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e(TAG, "Connected to GoogleApiClient");
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,
                "new location : " + location.getLatitude() + ", "
                        + location.getLongitude() + ". "
                        + location.getAccuracy() + " -> "
                        + location.getSpeed() + "");
        broadcastLocationFound(location);

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG,
                "Connection failed: ConnectionResult.getErrorCode() = "
                        + result.getErrorCode());
    }

    protected synchronized void buildGoogleApiClient() {
        Log.e(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constantes.UPDATE_INTERVAL);
        mLocationRequest
                .setFastestInterval(Constantes.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(getApplicationContext(),
                    Constantes.geofences_added, Toast.LENGTH_SHORT)
                    .show();
        } else {
            String errorMessage = getErrorString(this, status.getStatusCode());
            Toast.makeText(getApplicationContext(), errorMessage,
                    Toast.LENGTH_LONG).show();
        }
    }

    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return Constantes.geofence_not_available;
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return Constantes.geofence_too_many_geofences;
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return Constantes.geofence_too_many_pending_intents;
            default:
                return Constantes.unknown_geofence_error;
        }
    }

}
