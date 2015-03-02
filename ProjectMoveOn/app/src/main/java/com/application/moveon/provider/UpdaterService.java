package com.application.moveon.provider;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.GetCirclesService_Callback;
import com.application.moveon.rest.callback.GetMessage_Callback;
import com.application.moveon.rest.callback.UpdateCirlces_Callback;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 18/02/2015.
 */
public class UpdaterService extends Service {
    private PowerManager.WakeLock mWakeLock;
    private SessionManager session;
    private String idUser;
    private MoveOnDB db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "Updater Service");
        mWakeLock.acquire();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            stopSelf();
            return;
        }

        session = new SessionManager(UpdaterService.this);
        idUser = session.getUserDetails().get(SessionManager.KEY_EMAIL);
        db = new MoveOnDB(UpdaterService.this, session.getUserDetails().get(SessionManager.KEY_EMAIL));

        MoveOnService mosonchild;
        mosonchild = new RestClient(false).getApiService();
        mosonchild.getCercles(session.getUserDetails().get(SessionManager.KEY_EMAIL), new GetCirclesService_Callback(db));
        mosonchild.getAllMessages(session.getUserDetails().get(SessionManager.KEY_ID), new GetMessage_Callback(getBaseContext()));
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }
}