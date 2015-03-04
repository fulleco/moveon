package com.application.moveon.provider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
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
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by user on 18/02/2015.
 */
public class ProviderService extends Service implements GooglePlayServicesClient.ConnectionCallbacks {
    private PowerManager.WakeLock mWakeLock;
    private Context context = this;
    private NotificationManager notifManager;
    private SessionManager session;
    private String idUser;
    private MoveOnService mainmos;
    private LatLng currentPosition;
    private boolean canGetLocation;
    private LocationClient locationclient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {

        //Location l = getLocation();
        //currentPosition = new LatLng(l.getLatitude(), l.getLongitude());

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

        String pref_sync_key = getResources().getString(R.string.pref_sync_key);
        boolean notif = session.getPref().getBoolean(pref_sync_key,false);

        String pref_loc_key = getResources().getString(R.string.pref_loc_key);
        boolean location = session.getPref().getBoolean(pref_loc_key,false);

        if(location == true) {

            locationclient = new LocationClient(context, this, null);
            if (!locationclient.isConnected()) {
                locationclient.connect();
            } else {
                refreshPosition();
            }
        }

        if((idUser!=null)&&(idUser!="")&&(notif==true)) {
            mainmos.getmessages(idUser, new GetMessage_Callback(this));
            //mainmos.updateuser(session.getUserDetails().get(SessionManager.KEY_ID), currentPosition.latitude, currentPosition.longitude, new UpdatePosition_Callback());
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
    public void onConnected(Bundle bundle) {
        if(locationclient.isConnected()) {
            refreshPosition();
        }
    }

    public void refreshPosition(){
        Location l = locationclient.getLastLocation();
        currentPosition = new LatLng(l.getLatitude(), l.getLongitude());
        locationclient.disconnect();

        if ((idUser != null) || (idUser != "")) {
            //mainmos.getmessages(idUser, new GetMessage_Callback(this));
            mainmos.updateuser(session.getUserDetails().get(SessionManager.KEY_ID), currentPosition.latitude, currentPosition.longitude, new UpdatePosition_Callback());
        }
    }

    @Override
    public void onDisconnected() {

    }
}