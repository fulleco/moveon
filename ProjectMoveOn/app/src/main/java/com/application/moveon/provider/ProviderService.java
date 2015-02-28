package com.application.moveon.provider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.GetMessage_Callback;
import com.application.moveon.rest.callback.UpdatePosition_Callback;
import com.application.moveon.session.SessionManager;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by user on 18/02/2015.
 */
public class ProviderService extends Service implements LocationListener {
    private PowerManager.WakeLock mWakeLock;
    private Context context = this;
    private NotificationManager notifManager;
    private SessionManager session;
    private String idUser;
    private MoveOnService mainmos;
    private LatLng currentPosition;
    private boolean canGetLocation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {

        Location l = getLocation();
        currentPosition = new LatLng(l.getLatitude(), l.getLongitude());

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "Notification Service");
        mWakeLock.acquire();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            stopSelf();
            return;
        }

        session = new SessionManager(ProviderService.this);
        //session.checkLogin(false);
        idUser = session.getUserDetails().get(SessionManager.KEY_ID);
        //new NotifTask().execute();

        if((idUser!=null)||(idUser!="")) {
            mainmos.getmessages(idUser, new GetMessage_Callback(this));
            mainmos.updateuser(session.getUserDetails().get(SessionManager.KEY_ID), currentPosition.latitude, currentPosition.longitude, new UpdatePosition_Callback());
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RestClient r = new RestClient(false);
        mainmos = (new RestClient(true)).getApiService();
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    @Override
    public void onLocationChanged(Location location) {

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        currentPosition = new LatLng(latitude, longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public static long MIN_TIME_BW_UPDATES = 2000;
    public static float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    public Location getLocation() {

        Location location = null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.i("ANTHO", "pas de gps ni network");
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}