package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.R;
import com.application.moveon.friends.adapter.UserAdapter;
import com.application.moveon.rest.modele.DemandsPojo;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.Flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 13/01/2015.
 */
public class UpdateDemands_Callback implements Callback<DemandsPojo[]> {

    private MoveOnDB db;

    public UpdateDemands_Callback(MoveOnDB db) {
        this.db = db;
    }

    @Override
    public void success(DemandsPojo[] demandsPojos, Response response) {

        ArrayList<DemandsPojo> datas;
        if(demandsPojos != null) {
            datas = new ArrayList<DemandsPojo>(Arrays.asList(demandsPojos));
            db.open();
            db.updateDemands(datas);
            db.close();
        }else{
            datas = new ArrayList<DemandsPojo>();
        }


        Flags.setDemandflag(true);
        Flags.checkupdate();

    }

    @Override
    public void failure(RetrofitError error) {
        Log.i("MOVEON UPDATEDEMANDS", "NOT WORKING");
    }
}
