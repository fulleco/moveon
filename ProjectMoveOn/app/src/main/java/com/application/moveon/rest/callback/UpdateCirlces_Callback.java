package com.application.moveon.rest.callback;

import android.util.Log;

import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.DemandsPojo;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.Flags;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 26/02/2015.
 */
public class UpdateCirlces_Callback implements Callback<CerclePojo[]>{

    public UpdateCirlces_Callback() {
    }


    @Override
    public void success(CerclePojo[] cerclePojo, Response response) {
        MoveOnDB bdd = MoveOnDB.getInstance();
        ArrayList<CerclePojo> datas;
        if(cerclePojo != null) {
            datas = new ArrayList<CerclePojo>(Arrays.asList(cerclePojo));
        }else{
            datas = new ArrayList<CerclePojo>();
        }

        bdd.updateCircles(datas);

        Flags.setCircleflag(true);
        Flags.checkupdate();


    }

    @Override
    public void failure(RetrofitError error) {
        Log.i("MOVEON DB", "NOT WORKING");
    }
}
