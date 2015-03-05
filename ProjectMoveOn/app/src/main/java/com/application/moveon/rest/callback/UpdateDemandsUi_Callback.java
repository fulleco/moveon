package com.application.moveon.rest.callback;

import android.app.Activity;
import android.widget.ListView;

import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.friends.adapter.DemandsAdapter;
import com.application.moveon.rest.modele.DemandsPojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.Flags;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 05/03/2015.
 */
public class UpdateDemandsUi_Callback implements Callback<DemandsPojo[]> {

    private MoveOnDB db;
    private Activity a;
    private CustomProgressDialog p;
    private ListView lv;
    private SessionManager sm;

    public UpdateDemandsUi_Callback(MoveOnDB db, Activity a, CustomProgressDialog p, ListView lv) {
        this.db = db;
        this.a = a;
        this.p = p;
        this.lv = lv;
        this.sm = new SessionManager(a);
    }

    @Override
    public void success(DemandsPojo[] demandsPojos, Response response) {


        final ArrayList<DemandsPojo> datas;
        if(demandsPojos != null) {
            datas = new ArrayList<DemandsPojo>(Arrays.asList(demandsPojos));

        }else{
            datas = new ArrayList<DemandsPojo>();
        }

        db.open();
        db.updateDemands(datas);

        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(new DemandsAdapter(a, datas,sm.getUserDetails().get(SessionManager.KEY_EMAIL)));
                p.dismiss();
            }
        });
        db.close();


    }

    @Override
    public void failure(RetrofitError error) {
        p.dismiss();
    }
}
